const { Solution } = require("../models");

exports.createSolution = async (req, res) => {
  try {
    const solution = await Solution.create(req.body);
    res.status(201).json(solution);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

exports.getSolutions = async (req, res) => {
  try {
    const { question_id } = req.query;
    const where = question_id ? { question_id } : {};
    const solutions = await Solution.findAll({ where });
    res.json(solutions);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

