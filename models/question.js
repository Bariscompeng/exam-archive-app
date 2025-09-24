module.exports = (sequelize, DataTypes) => {
  return sequelize.define('Question', {
    exam_id: { type: DataTypes.INTEGER, allowNull: false },
    number: { type: DataTypes.INTEGER, allowNull: false },
    text: { type: DataTypes.TEXT, allowNull: false },
    image_url: { type: DataTypes.TEXT }
  }, {
    tableName: 'questions'
  });
};

