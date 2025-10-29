// models/user.js
const { DataTypes } = require('sequelize');
const bcrypt = require('bcrypt');

module.exports = (sequelize) => {
  const User = sequelize.define(
    'User',
    {
      // DB: id BIGINT (serial)
      id: {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        field: 'id',
      },
      // DB: name TEXT
      name: {
        type: DataTypes.TEXT,
        allowNull: false,
        field: 'name',
      },
      // DB: email TEXT (unique)
      email: {
        type: DataTypes.TEXT,
        allowNull: false,
        unique: true,
        validate: { isEmail: true },
        field: 'email',
      },
      // KOD: passwordHash  <->  DB: passwordhash
      passwordHash: {
        type: DataTypes.TEXT,
        allowNull: false,
        field: 'passwordhash',
      },
      // KOD: role  <->  DB: role
      role: {
        type: DataTypes.TEXT,
        allowNull: false,
        defaultValue: 'user',
        validate: { isIn: [['user', 'admin']] },
        field: 'role',
      },
    },
    {
      tableName: 'users',
      // DB'de küçük harf timestamp kolonları var
      timestamps: true,
      createdAt: 'createdat',
      updatedAt: 'updatedat',
    }
  );

  // Şifre doğrulama yardımcı metod
  User.prototype.verifyPassword = function (password) {
    return bcrypt.compare(password, this.passwordHash);
  };

  return User;
};

