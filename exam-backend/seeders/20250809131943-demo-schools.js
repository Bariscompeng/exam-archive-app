'use strict';

module.exports = {
  async up(queryInterface) {
    await queryInterface.bulkInsert('schools', [
      { name: 'Ankara Üniversitesi', createdAt: new Date(), updatedAt: new Date() },
      { name: 'Hacettepe Üniversitesi', createdAt: new Date(), updatedAt: new Date() }
    ]);
  },
  async down(queryInterface) {
    await queryInterface.bulkDelete('schools', null, {});
  }
};

