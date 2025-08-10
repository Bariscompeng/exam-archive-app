'use strict';

module.exports = {
  async up (queryInterface) {
    await queryInterface.bulkInsert('exams', [
      {
        name: 'NYP Final Sınavı',
        course_id: 1,
        date: new Date('2025-06-01T10:00:00Z'), // <-- ZORUNLU ALAN
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);
  },

  async down (queryInterface) {
    await queryInterface.bulkDelete('exams', null, {});
  }
};
