const { Question } = require("../models");

exports.createQuestion = async (req, res) => {
  try {
    const question = await Question.create(req.body);
    res.status(201).json(question);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

exports.getQuestions = async (req, res) => {
  try {
    const { exam_id } = req.query;
    const where = exam_id ? { exam_id } : {};
    const questions = await Question.findAll({ where });
    res.json(questions);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

