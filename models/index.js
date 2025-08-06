const { Sequelize, DataTypes } = require("sequelize");

const sequelize = new Sequelize("exam_app", "postgres", "postgres", {
  host: "localhost",
  dialect: "postgres",
});

const db = {};
db.Sequelize = Sequelize;
db.sequelize = sequelize;

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

module.exports = db;

