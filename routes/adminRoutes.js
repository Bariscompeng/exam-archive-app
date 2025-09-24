const express = require("express");
const router = express.Router();
const examController = require("../controllers/examController");
const questionController = require("../controllers/questionController");
const solutionController = require("../controllers/solutionController");
const { authMiddleware, adminOnly } = require("../middlewares/authMiddleware");

router.use(authMiddleware, adminOnly);

router.post("/exams", examController.createExam);
router.get("/exams", examController.getExams);

router.post("/questions", questionController.createQuestion);
router.get("/questions", questionController.getQuestions);

router.post("/solutions", solutionController.createSolution);
router.get("/solutions", solutionController.getSolutions);

module.exports = router;

