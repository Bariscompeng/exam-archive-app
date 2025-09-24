module.exports = (sequelize, DataTypes) => {
  return sequelize.define("School", {
    name: {
      type: DataTypes.TEXT,
      allowNull: false,
    }
  }, { tableName: "schools" });
};

