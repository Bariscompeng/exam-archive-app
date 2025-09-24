const { Sequelize, DataTypes } = require('sequelize');
require('dotenv').config();

const sequelize = new Sequelize(
  process.env.DB_NAME || 'exam_app_db',
  process.env.DB_USER || 'postgres',
  process.env.DB_PASSWORD || '25802009',
  {
    host: process.env.DB_HOST || '127.0.0.1',
    dialect: process.env.DB_DIALECT || 'postgres',
    logging: false,
  }
);

const db = {};
db.Sequelize = Sequelize;
db.sequelize = sequelize;

// Burada modelleri import et
db.School = require('./school')(sequelize, DataTypes);
db.User = require('./user')(sequelize, DataTypes);
// diğer modeller de aynı şekilde...

module.exports = db;

