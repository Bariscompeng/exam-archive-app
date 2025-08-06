module.exports = (sequelize, DataTypes) => {
  return sequelize.define('Exam', {
    course_id: { type: DataTypes.INTEGER, allowNull: false },
    school_id: { type: DataTypes.INTEGER, allowNull: false },
    year: { type: DataTypes.INTEGER, allowNull: false },
    semester: { type: DataTypes.TEXT, allowNull: false },
    type: { type: DataTypes.TEXT, allowNull: false }
  }, {
    tableName: 'exams'
  });
};

