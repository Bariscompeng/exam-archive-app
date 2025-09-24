// routes/favoritesRoutes.js
const express = require("express");
const router = express.Router();

// Basit in-memory favoriler
let favorites = [
  {
    id: "fav1",
    lessonId: "MATH101",
    text: "2+2 kaç eder?",
    options: ["1", "2", "3", "4"]
  }
];

// GET /api/favorites
router.get("/favorites", (req, res) => {
  res.json(favorites);
});

// POST /api/favorites  body: { questionId: "..." } veya esnek map
router.post("/favorites", (req, res) => {
  const body = req.body || {};
  // Örnek bir ekleme – gerçek sistemde questionId ile DB'den soru çekersin
  const fav = {
    id: `fav${Date.now()}`,
    lessonId: body.lessonId || "GEN101",
    text: body.text || "Örnek favori sorusu",
    options: body.options || ["A", "B", "C", "D"]
  };
  favorites.push(fav);
  res.json({ message: "Favori eklendi", id: fav.id });
});

// DELETE /api/favorites/:id
router.delete("/favorites/:id", (req, res) => {
  const { id } = req.params;
  const before = favorites.length;
  favorites = favorites.filter(f => f.id !== id);
  if (favorites.length === before) {
    return res.status(404).json({ message: "Favori bulunamadı" });
  }
  res.json({ message: "Favori silindi" });
});

module.exports = router;

