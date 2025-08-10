'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.bulkInsert('courses', [
      {
        name: 'Nesne Yönelimli Programlama',
        code: 'CSE201', // NOT NULL olduğu için ekledik
        department_id: 1, // ilgili department id
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        name: 'Veritabanı Sistemleri',
        code: 'CSE301',
        department_id: 1,
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);
  },

  async down(queryInterface, Sequelize) {
    await queryInterface.bulkDelete('courses', null, {});
  }
};
