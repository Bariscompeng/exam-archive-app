module.exports = (sequelize, DataTypes) => {
  return sequelize.define('Solution', {
    question_id: { type: DataTypes.INTEGER, allowNull: false },
    text: { type: DataTypes.TEXT, allowNull: false },
    image_url: { type: DataTypes.TEXT }
  }, {
    tableName: 'solutions'
  });
};

