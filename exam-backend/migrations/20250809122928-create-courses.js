'use strict';
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('courses', {
      id: { allowNull: false, autoIncrement: true, primaryKey: true, type: Sequelize.INTEGER },
      name: { type: Sequelize.TEXT, allowNull: false },
      code: { type: Sequelize.TEXT, allowNull: false },
      department_id: {
        type: Sequelize.INTEGER, allowNull: false,
        references: { model: 'departments', key: 'id' },
        onUpdate: 'CASCADE', onDelete: 'RESTRICT'
      },
      createdAt: { allowNull: false, type: Sequelize.DATE },
      updatedAt: { allowNull: false, type: Sequelize.DATE }
    });
    await queryInterface.addIndex('courses', ['department_id']);
    await queryInterface.addIndex('courses', ['code'], { unique: false });
  },
  async down(queryInterface) {
    await queryInterface.dropTable('courses');
  }
};
