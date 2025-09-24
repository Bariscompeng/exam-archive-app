<<<<<<< HEAD
// server.js
const express = require("express");
const cors = require("cors");
=======
const express = require("express");
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
const app = express();
require("dotenv").config();

const db = require("./models");

<<<<<<< HEAD
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

=======
const publicRoutes = require("./routes/publicRoutes");
const userRoutes = require("./routes/userRoutes");

app.use(express.json());

app.use("/api", publicRoutes);
app.use("/api", userRoutes);

db.sequelize.sync().then(() => {
  app.listen(3000, () => {
    console.log("✅ Sunucu 3000 portunda çalışıyor");
  });
});
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
