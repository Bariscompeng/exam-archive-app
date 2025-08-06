const express = require("express");
const app = express();
require("dotenv").config();

const db = require("./models");

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
