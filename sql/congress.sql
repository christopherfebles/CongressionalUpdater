CREATE TABLE `Congress` (
  `bioguide_id` varchar(10) NOT NULL DEFAULT '',
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `photo_url` varchar(255) NOT NULL,
  `photo` longblob,
  PRIMARY KEY (`bioguide_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;