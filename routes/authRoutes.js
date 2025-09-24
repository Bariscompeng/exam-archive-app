// routes/authRoutes.js
const express = require("express");
const router = express.Router();

// POST /api/auth/login
router.post("/auth/login", (req, res) => {
  const { email, password } = req.body || {};
  if (!email || !password) {
    return res.status(400).json({ message: "email ve password zorunlu" });
  }
  // Dummy response: AuthResponse
  return res.json({
    token: "dummy-token-123",
    user: { id: "1", name: "Test Kullanıcı", email }
  });
});

// POST /api/auth/register
router.post("/auth/register", (req, res) => {
  const { name, email, password } = req.body || {};
  if (!name || !email || !password) {
    return res.status(400).json({ message: "name, email, password zorunlu" });
  }
  return res.json({
    token: "dummy-token-registered",
    user: { id: "2", name, email }
  });
});

// GET /api/auth/me
router.get("/auth/me", (req, res) => {
  // Normalde Authorization header'dan token doğrulanır.
  return res.json({ id: "1", name: "Test Kullanıcı", email: "test@test.com" });
});

module.exports = router;

