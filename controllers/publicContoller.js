const { School, Department, Course } = require("../models");

exports.getSchools = async (req, res) => {
  const schools = await School.findAll();
  res.json(schools);
};

exports.getDepartmentsBySchool = async (req, res) => {
  const departments = await Department.findAll({
    where: { school_id: req.params.id },
  });
  res.json(departments);
};

exports.getCoursesByDepartment = async (req, res) => {
  const courses = await Course.findAll({
    where: { department_id: req.params.id },
  });
  res.json(courses);
};
