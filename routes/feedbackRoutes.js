// routes/feedbackRoutes.js
const express = require("express");
const router = express.Router();

// Basit in-memory liste
const feedbacks = [
  { id: "f1", email: "melihberat00@gmail.com", message: "Harika uygulama!" },
  { id: "f2", email: "ayse@example.com", message: "Arayüz güzel." }
];

// GET /api/feedbacks
router.get("/feedbacks", (req, res) => {
  res.json(feedbacks);
});

// POST /api/feedback
router.post("/feedback", (req, res) => {
  const { email, message } = req.body || {};
  if (!email || !message) {
    return res.status(400).json({ message: "email ve message zorunlu" });
  }
  const f = { id: `f${Date.now()}`, email, message };
  feedbacks.push(f);
  res.json({ message: "Feedback alındı", id: f.id });
});

module.exports = router;

