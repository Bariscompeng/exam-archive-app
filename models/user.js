module.exports = (sequelize, DataTypes) => {
  return sequelize.define("User", {
    username: DataTypes.TEXT,
    email: DataTypes.TEXT,
    password_hash: DataTypes.TEXT,
    role: DataTypes.TEXT,
    school_id: DataTypes.INTEGER
  }, { tableName: "users" });
};

