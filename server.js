// server.js
const express = require("express");
const cors = require("cors");
const app = express();
require("dotenv").config();

const db = require("./models");

// Var olan router'lar
const publicRoutes = require("./routes/publicRoutes");
const userRoutes = require("./routes/userRoutes");

// Yeni ekleyeceklerimiz
const authRoutes = require("./routes/authRoutes");
const feedbackRoutes = require("./routes/feedbackRoutes");
const favoritesRoutes = require("./routes/favoritesRoutes");

app.use(cors());
app.use(express.json());

// Basit health check
app.get("/api/health", (req, res) => res.json({ ok: true }));

// Mount routes
app.use("/api", publicRoutes);
app.use("/api", userRoutes);
app.use("/api", authRoutes);
app.use("/api", feedbackRoutes);
app.use("/api", favoritesRoutes);

db.sequelize.sync().then(() => {
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => {
    console.log(`✅ Sunucu ${PORT} portunda çalışıyor`);
  });
});

