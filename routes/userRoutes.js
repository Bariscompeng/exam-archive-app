// routes/userRoutes.js
const express = require("express");
const router = express.Router();
const jwt = require("jsonwebtoken");
const db = require("../models");

// Token zorunlu middleware
function requireAuth(req, res, next) {
  const auth = req.headers.authorization || "";
  const [, token] = auth.split(" ");
  if (!token) return res.status(401).json({ message: "Unauthorized" });

  try {
    const payload = jwt.verify(token, process.env.JWT_SECRET);
    req.user = { id: payload.sub, email: payload.email, role: payload.role || "user" };
    next();
  } catch (e) {
    return res.status(401).json({ message: "Invalid token" });
  }
}

// KULLANICI HESABINI SİL
router.delete("/users/me", requireAuth, async (req, res) => {
  const userId = req.user.id;

  try {
    // Sizin projede model anahtarı User / Users / user olabilir; hepsine toleranslı
    const User = db.User || db.Users || db.user;
    if (!User) {
      console.error("User modeli bulunamadı. Modeller:", Object.keys(db));
      return res.status(500).json({ message: "User modeli yok" });
    }

    const user = await User.findByPk(userId);
    if (!user) return res.status(404).json({ message: "User not found" });

    await user.destroy(); // paranoid=false ise hard delete; true ise soft delete
    return res.json({ message: "Account deleted" });
  } catch (err) {
    console.error("DELETE /users/me failed:", err);
    return res.status(500).json({ message: "Server error", error: err.message });
  }
});

module.exports = router;

