'use strict';

module.exports = {
  async up (queryInterface) {
    await queryInterface.bulkInsert('departments', [
      {
        name: 'Bilgisayar Mühendisliği',
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);
  },

  async down (queryInterface) {
    await queryInterface.bulkDelete('departments', null, {});
  }
};
