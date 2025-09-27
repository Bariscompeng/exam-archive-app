// models/index.js
const { Sequelize } = require('sequelize');
const path = require('path');

const sequelize = process.env.DATABASE_URL
  ? new Sequelize(process.env.DATABASE_URL, { logging: false })
  : new Sequelize({
      dialect: 'sqlite',
      storage: path.join(__dirname, '..', 'db.sqlite'),
      logging: false,
    });

const db = {};
db.Sequelize = Sequelize;
db.sequelize = sequelize;

// Modeller
db.User = require('./user')(sequelize);

module.exports = db;

