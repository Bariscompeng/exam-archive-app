module.exports = (sequelize, DataTypes) => {
  return sequelize.define("Department", {
    name: {
      type: DataTypes.TEXT,
      allowNull: false,
    },
    school_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
    }
  }, { tableName: "departments" });
};

