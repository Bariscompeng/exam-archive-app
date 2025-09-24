const { User } = require("../models");
const bcrypt = require("bcrypt");

exports.register = async (req, res) => {
  const { username, email, password, role, school_id } = req.body;
  const password_hash = await bcrypt.hash(password, 10);
  const user = await User.create({ username, email, password_hash, role, school_id });
  res.status(201).json(user);
};
