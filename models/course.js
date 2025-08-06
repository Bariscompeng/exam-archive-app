module.exports = (sequelize, DataTypes) => {
  return sequelize.define("Course", {
    name: {
      type: DataTypes.TEXT,
      allowNull: false,
    },
    code: {
      type: DataTypes.TEXT,
      allowNull: false,
    },
    department_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
    }
  }, { tableName: "courses" });
};

