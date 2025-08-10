'use strict';
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('questions', {
      id: { 
        allowNull: false, 
        autoIncrement: true, 
        primaryKey: true, 
        type: Sequelize.INTEGER 
      },
      exam_id: {
        type: Sequelize.INTEGER,
        allowNull: false,
        references: { model: 'exams', key: 'id' },
        onUpdate: 'CASCADE',
        onDelete: 'CASCADE'
      },
      text: { 
        type: Sequelize.TEXT, 
        allowNull: true 
      },
      image_url: { 
        type: Sequelize.TEXT, 
        allowNull: true 
      },
      createdAt: { 
        allowNull: false, 
        type: Sequelize.DATE 
      },
      updatedAt: { 
        allowNull: false, 
        type: Sequelize.DATE 
      }
    });

    await queryInterface.addIndex('questions', ['exam_id']);
  },

  async down(queryInterface) {
    await queryInterface.dropTable('questions');
  }
};
