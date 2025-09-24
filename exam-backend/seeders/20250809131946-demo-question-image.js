'use strict';

module.exports = {
  async up(queryInterface) {
    await queryInterface.bulkInsert('questions', [
      { exam_id: 1, image_url: 'uploads/questions/oop.jpeg', createdAt: new Date(), updatedAt: new Date() }
    ]);
  },
  async down(queryInterface) {
    await queryInterface.bulkDelete('questions', null, {});
  }
};

