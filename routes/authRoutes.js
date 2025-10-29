// routes/authRoutes.js
const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const db = require('../models');
require('dotenv').config();

const JWT_SECRET = process.env.JWT_SECRET || 'dev_secret';
const ADMIN_INVITE_CODE = process.env.ADMIN_INVITE_CODE || null;
const ADMIN_EMAILS = (process.env.ADMIN_EMAILS || '')
  .split(',')
  .map(s => s.trim())
  .filter(Boolean);

// İlk rolü belirlemek için (register sırasında)
function computeInitialRole(email, inviteCode) {
  if (inviteCode && ADMIN_INVITE_CODE && inviteCode === ADMIN_INVITE_CODE) return 'admin';
  if (ADMIN_EMAILS.includes(email)) return 'admin';
  return 'user';
}

function signToken(user) {
  return jwt.sign(
    { sub: user.id, email: user.email, role: user.role || 'user' },
    JWT_SECRET,
    { expiresIn: '7d' }
  );
}

// POST /api/auth/register
router.post('/auth/register', async (req, res) => {
  try {
    const { name, email, password, inviteCode } = req.body || {};
    if (!name || !email || !password) {
      return res.status(400).json({ message: 'name, email, password zorunlu' });
    }

    const exists = await db.User.findOne({ where: { email } });
    if (exists) {
      return res.status(409).json({ message: 'Bu e-posta zaten kayıtlı' });
    }

    const role = computeInitialRole(email, inviteCode);
    const passwordHash = await bcrypt.hash(password, 10);

    // Artık DB'de role kolonu var -> role'ü yazarak oluşturuyoruz
    const user = await db.User.create({ name, email, passwordHash, role });

    const token = signToken(user);
    console.log('📝 Kayıt:', { id: user.id, email: user.email, role: user.role });
    return res.json({
      token,
      user: { id: String(user.id), name: user.name, email: user.email, role: user.role }
    });
  } catch (e) {
    console.error('register hata:', e);
    return res.status(500).json({ message: 'Sunucu hatası' });
  }
});

// POST /api/auth/login
router.post('/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body || {};
    if (!email || !password) {
      return res.status(400).json({ message: 'email ve password zorunlu' });
    }

    const user = await db.User.findOne({ where: { email } });
    if (!user) return res.status(401).json({ message: 'Geçersiz kimlik bilgileri' });

    const ok = typeof user.verifyPassword === 'function'
      ? await user.verifyPassword(password)
      : await bcrypt.compare(password, user.passwordHash);

    if (!ok) return res.status(401).json({ message: 'Geçersiz kimlik bilgileri' });

    const token = signToken(user);
    console.log('🔑 Login:', { id: user.id, email: user.email, role: user.role });
    return res.json({
      token,
      user: { id: String(user.id), name: user.name, email: user.email, role: user.role }
    });
  } catch (e) {
    console.error('login hata:', e);
    return res.status(500).json({ message: 'Sunucu hatası' });
  }
});

// GET /api/auth/me
router.get('/auth/me', async (req, res) => {
  try {
    const auth = req.headers.authorization || '';
    const token = auth.startsWith('Bearer ') ? auth.slice(7) : null;
    if (!token) return res.status(401).json({ message: 'Yetkisiz' });

    const payload = jwt.verify(token, JWT_SECRET);
    const user = await db.User.findByPk(payload.sub);
    if (!user) return res.status(401).json({ message: 'Yetkisiz' });

    return res.json({
      id: String(user.id),
      name: user.name,
      email: user.email,
      role: user.role || 'user'
    });
  } catch (e) {
    console.error('me hata:', e);
    return res.status(401).json({ message: 'Yetkisiz' });
  }
});

module.exports = router;

