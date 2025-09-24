<<<<<<< HEAD
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
=======
const { Sequelize, DataTypes } = require("sequelize");

const sequelize = new Sequelize("exam_app", "postgres", "postgres", {
  host: "localhost",
  dialect: "postgres",
});
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c

const db = {};
db.Sequelize = Sequelize;
db.sequelize = sequelize;

<<<<<<< HEAD
// Burada modelleri import et
db.School = require('./school')(sequelize, DataTypes);
db.User = require('./user')(sequelize, DataTypes);
// diğer modeller de aynı şekilde...
=======
// Modelleri tanımla
db.School = require("./school")(sequelize, DataTypes);
db.Department = require("./department")(sequelize, DataTypes);
db.Course = require("./course")(sequelize, DataTypes);
db.User = require("./user")(sequelize, DataTypes);

// İlişkileri tanımla
db.School.hasMany(db.Department, { foreignKey: "school_id" });
db.Department.belongsTo(db.School, { foreignKey: "school_id" });

db.Department.hasMany(db.Course, { foreignKey: "department_id" });
db.Course.belongsTo(db.Department, { foreignKey: "department_id" });

db.School.hasMany(db.User, { foreignKey: "school_id" });
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c

module.exports = db;

