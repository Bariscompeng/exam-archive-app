const express = require("express");
const router = express.Router();
const publicController = require("../controllers/publicController");

router.get("/schools", publicController.getSchools);
router.get("/schools/:id/departments", publicController.getDepartmentsBySchool);
router.get("/departments/:id/courses", publicController.getCoursesByDepartment);

module.exports = router;
