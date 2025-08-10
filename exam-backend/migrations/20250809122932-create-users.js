'use strict';
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('users', {
      id: { allowNull: false, autoIncrement: true, primaryKey: true, type: Sequelize.INTEGER },
      username: { type: Sequelize.TEXT, allowNull: false },
      email: { type: Sequelize.TEXT, allowNull: false },
      password_hash: { type: Sequelize.TEXT, allowNull: false },
      role: { type: Sequelize.TEXT, allowNull: false }, // 'admin' / 'ogrenci' vb.
      createdAt: { allowNull: false, type: Sequelize.DATE },
      updatedAt: { allowNull: false, type: Sequelize.DATE }
    });
    await queryInterface.addIndex('users', ['email'], { unique: true });
    await queryInterface.addIndex('users', ['username'], { unique: true });
  },
  async down(queryInterface) {
    await queryInterface.dropTable('users');
  }
};
