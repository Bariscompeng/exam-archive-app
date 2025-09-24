'use strict';
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('solutions', {
      id: { 
        allowNull: false, 
        autoIncrement: true, 
        primaryKey: true, 
        type: Sequelize.INTEGER 
      },
      question_id: {
        type: Sequelize.INTEGER, 
        allowNull: false,
        references: { model: 'questions', key: 'id' },
        onUpdate: 'CASCADE', 
        onDelete: 'CASCADE'
      },
      text: { type: Sequelize.TEXT, allowNull: true },
      image_url: { type: Sequelize.TEXT, allowNull: true },
      createdAt: { allowNull: false, type: Sequelize.DATE },
      updatedAt: { allowNull: false, type: Sequelize.DATE }
    });
    await queryInterface.addIndex('solutions', ['question_id']);
  },
  async down(queryInterface) {
    await queryInterface.dropTable('solutions');
  }
};
