-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: cuahangdogo
-- ------------------------------------------------------
-- Server version	8.4.6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(255) NOT NULL,
  `commune` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `village` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1fa36y2oqhao3wgg2rw1pi459` (`user_id`),
  CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (2,'Bac Ninh','Tu Son','2025-11-17 17:12:39.368739','Quan bun cha Huong Son','Dinh Bang',1),(4,'Hà Nội','Xã Thư Lâm','2025-11-22 00:44:38.401347','Bia hơi Định Nguyệt','Thôn Châu Phong',5),(5,'Hà Nội','Xã Thư Lâm','2025-11-27 01:43:53.971348','Ngõ Ngánh','Thôn Châu Phong',4),(6,'Hà Nội','Xã Thư Lâm','2025-12-23 03:34:52.356742','Quán bia ngõ mới','Thôn Châu Phong',1),(7,'Hà Nội','Xã Thư Lâm','2025-12-23 12:17:20.928230','Ngõ Tây','Thôn Châu Phong',6),(8,'Hà Nội','Xã Thư Lâm','2025-12-25 02:34:42.504924','Ngõ Mới','Thôn Châu Phong',7),(9,'Hà Nội','Xã Đông Anh','2026-01-14 16:56:20.155180','Trà Huế xin chào','Thôn Đoài',8),(10,'Hà Nội','Xã Đông Anh','2026-01-19 01:46:14.125943','Ngã 3 Giày Da','Thôn Đoài',10);
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `quantity` int NOT NULL,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpcttvuq4mxppo8sxggjtn5i2c` (`cart_id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK64t7ox312pqal3p7fg9o503c2` (`user_id`),
  CONSTRAINT `FKb5o626f86h46m4s7ms6ginnop` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,'2025-11-15 02:18:32.873474',1),(2,'2025-11-15 02:29:24.473205',3),(3,'2025-11-21 23:43:21.449553',4),(4,'2025-11-21 23:52:20.567619',5),(5,'2025-12-23 12:16:45.064837',6),(6,'2025-12-25 02:34:07.375020',7),(7,'2026-01-14 16:55:25.849146',8),(9,'2026-01-19 01:45:10.429446',10);
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'2025-11-15 02:43:48.909631','Hương đá','https://res.cloudinary.com/deurw9jqh/image/upload/v1768448069/categories/h1sowojd6c7dfa6j20if.jpg'),(2,'2025-11-15 02:44:00.287437','Xoan Đào','https://res.cloudinary.com/deurw9jqh/image/upload/v1768448132/categories/ssphc42ga7wwxf8xkegr.jpg'),(3,'2025-11-15 02:44:08.750954','Sưa','https://res.cloudinary.com/deurw9jqh/image/upload/v1768448671/categories/z6sre30eyhtzh5lca7rl.jpg'),(4,'2025-11-15 02:44:12.231310','Trắc','https://res.cloudinary.com/deurw9jqh/image/upload/v1768448717/categories/ehkqjwcfciffds2albee.jpg'),(5,'2025-11-15 02:44:20.986712','Hoàng đàn tuyết Lạng Sơn','https://res.cloudinary.com/deurw9jqh/image/upload/v1768448765/categories/jpg2mio8evaqayamurof.jpg');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (1,'Gỗ hương đá thuộc loại cây họ đậu, có tên khoa học là Pterocarpus Macrocarpus. Đường kính của gỗ trung bình từ 0,7 – 0,9m, có khi cao đến 1,2m. Gỗ hương đá sinh trưởng và phát triển mạnh mẽ trong điều kiện thời tiết khắc nghiệt và nắng nóng. \r\n\r\nLoại gỗ này thường được trồng ở các nước Đông Nam Á như Việt Nam, Campuchia, Lào,…và các nước ở vùng Nam Phi. Ở Việt Nam, gỗ hương đá được phân bố ở vùng đất Tây Nguyên như: Gia Lai, Kon Tum, Đắk Lắk.\r\n\r\nGỗ hương đá là dòng gỗ hương thuộc nhóm I trong bảng phân loại gỗ. Đây là loại gỗ tự nhiên quý hiếm và được quản lý nghiêm ngặt. Giá gỗ hương đá dao động từ 26 -  32.000.000 vnđ/m3. Vì gỗ có độ bền cao nên thường dùng để chế tác các loại đồ dùng như bàn ghế, bàn thờ, giường ngủ, tủ giày,…','2026-01-19 01:31:05.420557','http://res.cloudinary.com/deurw9jqh/image/upload/v1768761064/news/xcph5l2glmtm6swro35i.jpg','Gỗ hương đá là gì? Gỗ hương đá có mấy loại ?','2026-01-19 01:31:05.420557'),(2,'Gỗ trắc còn có tên gọi khác là gỗ cầm lai, là loại gỗ thân to cao, có mùi chua, được phân bố chủ yếu ở miền Trung như các tỉnh Quảng Trị, Quảng Nam và rải rác ở khu vực Nam Bộ. Thân cây to có độ cao trung bình khoảng 25m, đường kính khoảng 80-100cm. Vỏ cây nhẵn màu nâu xám nhiều xơ. Là một loại cây rất thích ánh sáng được trồng chủ yếu ở những nơi có độ cao từ 500m trở lên.\r\nGỗ trắc thuộc nhóm I trong nhóm gỗ quý của Việt Nam, là loại gỗ quý hiếm, đẹp, có hương thơm và rất có giá trị.','2026-01-19 01:38:38.843168','http://res.cloudinary.com/deurw9jqh/image/upload/v1768761517/news/fligw1ph0ibuueokiuup.jpg','Gỗ trắc có tốt không? Đặc điểm, nhận biết và ứng dụng','2026-01-19 01:38:38.843168'),(3,'Gỗ sưa có tên tiếng anh là Dalbergia Odorifera, ở Việt Nam thường được gọi là huỳnh đàn,gỗ huê hay trắc thối, là chất liệu gỗ tự nhiên được khai thác từ cây sưa – loài thực vật thân gỗ, nhóm họ Đậu. Gỗ sưa là loại gỗ quý và thuộc hàng siêu hiếm. Gỗ có chất lượng tốt, thớ gỗ mịn, đường vân đẹp.\r\n\r\nĐặc biệt, gỗ có hương thơm tự nhiên, thoáng nhẹ tựa như hương trầm. Nhờ những đặc điểm trên, loại gỗ này được đánh giá là chất liệu thượng hạng trong thiết kế thi công nội thất.','2026-01-19 01:39:52.623233','http://res.cloudinary.com/deurw9jqh/image/upload/v1768761591/news/mpdcgbflgkhve372rvn9.jpg','Gỗ sưa là gì? Tác dụng – Tại sao gỗ sưa đỏ lại đắt?','2026-01-19 01:39:52.623233');
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `price_at_purchase` bigint NOT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,'2025-11-17 18:45:27.207237',7500000,1,1,2,NULL),(2,'2025-11-17 18:45:27.207237',12500000,1,1,3,NULL),(3,'2025-11-17 19:24:37.994533',12500000,1,2,3,'Sofa gỗ xoan đào cao cấp'),(4,'2025-11-17 19:24:53.200411',7500000,1,3,2,'Kệ tivi phẳng gỗ hương đá'),(5,'2025-11-22 00:45:41.370584',7500000,1,4,2,'Kệ tivi phẳng gỗ hương đá'),(6,'2025-11-26 02:16:00.989705',7500000,1,5,2,'Kệ tivi phẳng gỗ hương đá'),(7,'2025-11-27 01:44:04.795237',7500000,1,6,2,'Kệ tivi phẳng gỗ hương đá'),(8,'2025-12-02 01:51:39.186582',8500000,1,7,10,'Giường ngủ'),(9,'2025-12-19 11:02:09.573682',50000000,10,8,9,'Bộ bàn ghế Tần Thủy Hoàng'),(10,'2025-12-23 03:33:48.654862',100000000,1,9,11,'Tượng phật quan âm hoàng đàn tuyết'),(11,'2025-12-23 03:35:31.495301',3000000,1,10,5,'Vòng tay gỗ hoàng đàn tuyết Lạng Sơn'),(12,'2025-12-23 04:15:49.361987',5800000,1,11,15,'Bàn ăn xoan đào chữ nhật 1m6 6 ghế'),(13,'2025-12-23 07:48:03.511352',300000,1,12,8,'Vòng tay gỗ Sưa đỏ'),(14,'2025-12-23 10:40:06.731672',10000000,1,13,7,'Tượng phật di lạc'),(15,'2025-12-23 12:17:36.157238',12500000,1,14,3,'Sofa gỗ xoan đào cao cấp'),(16,'2025-12-25 01:34:39.353254',300000,1,15,8,'Vòng tay gỗ Sưa đỏ'),(17,'2025-12-25 01:50:12.765421',2000000,1,16,19,'Bút gỗ sưa đỏ Quảng Bình vân siêu vip'),(18,'2025-12-25 02:00:43.846461',3000000,1,17,5,'Vòng tay gỗ hoàng đàn tuyết Lạng Sơn'),(19,'2025-12-25 02:08:00.101030',16000000,1,18,14,'Đồng hồ tứ trụ'),(20,'2025-12-25 02:11:26.708441',10000000,1,19,7,'Tượng phật di lạc'),(21,'2025-12-25 02:11:54.371274',10000000,1,20,7,'Tượng phật di lạc'),(22,'2025-12-25 02:12:42.595440',7500000,1,21,2,'Kệ tivi phẳng gỗ hương đá'),(23,'2025-12-25 02:18:07.010286',300000,1,22,8,'Vòng tay gỗ Sưa đỏ'),(24,'2025-12-25 02:22:50.345857',5800000,1,23,15,'Bàn ăn xoan đào chữ nhật 1m6 6 ghế'),(25,'2025-12-25 02:28:58.704276',50000000,1,24,9,'Bộ bàn ghế Tần Thủy Hoàng'),(26,'2025-12-25 02:34:48.249628',4000000,1,25,20,'Bộ ấm chén gỗ sưa'),(27,'2025-12-25 02:39:45.610522',8500000,1,26,10,'Giường ngủ'),(28,'2025-12-25 08:51:34.944296',4500000,1,27,24,'Độc mã Gỗ trắc đỏ đen gỗ trắc Việt Nam'),(29,'2026-01-14 16:57:30.610534',3400000,1,28,28,'Lộc Bình Gỗ Trắc Vân Tuyệt Đẹp'),(30,'2026-01-15 13:19:23.696761',2000000,1,29,19,'Bút gỗ sưa đỏ Quảng Bình vân siêu vip'),(31,'2026-01-19 01:46:25.839680',3400000,1,30,28,'Lộc Bình Gỗ Trắc Vân Tuyệt Đẹp'),(32,'2026-01-19 09:47:15.311265',60000000,1,31,25,'Bộ Bàn Ghế Gỗ Trắc'),(33,'2026-01-19 09:47:50.423782',3400000,1,32,28,'Lộc Bình Gỗ Trắc Vân Tuyệt Đẹp'),(34,'2026-01-26 09:14:34.553933',4500000,1,33,24,'Độc mã Gỗ trắc đỏ đen gỗ trắc Việt Nam');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `status` enum('CANCELLED','CONFIRMED','DELIVERED','PENDING','SHIPPING') NOT NULL,
  `total_amount` bigint DEFAULT NULL,
  `shipping_address_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `customer_note` varchar(255) DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `vnp_response_code` varchar(255) DEFAULT NULL,
  `vnp_secure_hash` varchar(255) DEFAULT NULL,
  `vnp_transaction_no` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmk6q95x8ffidq82wlqjaq7sqc` (`shipping_address_id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmk6q95x8ffidq82wlqjaq7sqc` FOREIGN KEY (`shipping_address_id`) REFERENCES `addresses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2025-11-17 18:45:27.207237','DELIVERED',20000000,2,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'2025-11-17 19:24:37.993532','CANCELLED',12500000,2,1,'Xin vui lòng gọi trước khi giao.',NULL,NULL,NULL,NULL,NULL,NULL),(3,'2025-11-17 19:24:53.200411','DELIVERED',7500000,2,1,'Xin vui lòng gọi trước khi giao.',NULL,NULL,NULL,NULL,NULL,NULL),(4,'2025-11-22 00:45:41.369596','DELIVERED',7500000,4,5,'giao lúc ăn cơm',NULL,NULL,NULL,NULL,NULL,NULL),(5,'2025-11-26 02:16:00.989705','DELIVERED',7500000,2,1,'',NULL,NULL,NULL,NULL,NULL,NULL),(6,'2025-11-27 01:44:04.795237','DELIVERED',7500000,5,4,'',NULL,NULL,NULL,NULL,NULL,NULL),(7,'2025-12-02 01:51:39.186582','CANCELLED',8500000,2,1,'',NULL,NULL,NULL,NULL,NULL,NULL),(8,'2025-12-19 11:02:09.573682','CANCELLED',500000000,2,1,'',NULL,NULL,NULL,NULL,NULL,NULL),(9,'2025-12-23 03:33:48.654862','CANCELLED',100000000,2,1,'a cứ mở cửa lên rồi để ở bàn',NULL,NULL,NULL,NULL,NULL,NULL),(10,'2025-12-23 03:35:31.495301','DELIVERED',3000000,6,1,'',NULL,NULL,NULL,NULL,NULL,NULL),(11,'2025-12-23 04:15:49.361987','DELIVERED',5800000,4,5,'',NULL,NULL,NULL,NULL,NULL,NULL),(12,'2025-12-23 07:48:03.511352','DELIVERED',300000,5,4,'',NULL,NULL,NULL,NULL,NULL,NULL),(13,'2025-12-23 10:40:06.731672','DELIVERED',10000000,6,1,'',NULL,NULL,NULL,NULL,NULL,NULL),(14,'2025-12-23 12:17:36.157238','CANCELLED',12500000,7,6,'gọi trước khi giao',NULL,NULL,NULL,NULL,NULL,NULL),(15,'2025-12-25 01:34:39.353254','CANCELLED',300000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(16,'2025-12-25 01:50:12.765421','CANCELLED',2000000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(17,'2025-12-25 02:00:43.846461','CANCELLED',3000000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(18,'2025-12-25 02:08:00.100598','CANCELLED',16000000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(19,'2025-12-25 02:11:26.708441','CANCELLED',10000000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(20,'2025-12-25 02:11:54.371274','CANCELLED',10000000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(21,'2025-12-25 02:12:42.595440','CANCELLED',7500000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(22,'2025-12-25 02:18:07.006335','CANCELLED',300000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(23,'2025-12-25 02:22:50.345857','CANCELLED',5800000,5,4,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(24,'2025-12-25 02:28:58.704276','CONFIRMED',50000000,5,4,'','2025-12-25 02:29:28.941786','VNPAY','PAID','00','5a77a556343089f4c4f8626b892c90d8b0fb0c4caab3de6b636fa943158534b1d8bd7312702425b96ba562ac2a5391732cb05bd4935fc8852a4649fb6d542661','15367085'),(25,'2025-12-25 02:34:48.249628','CONFIRMED',4000000,8,7,'','2025-12-25 02:35:23.387432','VNPAY','PAID','00','20162c529f96824cba1861706fd04b18167d6f8cef252e4532b15d23d82ebde0312ca347b2a0c215b06d9f38317286ad7e05f38fe3cde8a671e293616533fae0','15367087'),(26,'2025-12-25 02:39:45.610522','CONFIRMED',8500000,8,7,'','2025-12-25 02:40:17.636248','VNPAY','PAID','00','9e1c7c4f5b59e0aba26ea9fe5c292ff999c39ad79834ee0eaba1f63c2330d1601ead3161a7244eef4d643c92084cf1012c5c719aab2d84b3320aea71b99ef680','15367090'),(27,'2025-12-25 08:51:34.944296','DELIVERED',4500000,8,7,'','2025-12-25 08:52:04.393071','VNPAY','PAID','00','a6ee58c4edd1acae8e6bf18b2b0ecb74827c65878582d6382e5571e856362a6af15f56fab493220dd0af8c1c28fe9e5e1b11b81ed2c73c3c198ae32892a3165c','15367261'),(28,'2026-01-14 16:57:30.609430','DELIVERED',3400000,9,8,'cứ mở cửa mang vào nhà','2026-01-14 16:58:39.763484','VNPAY','PAID','00','43279f063c75617ab6f931888139a22b28093d0b514e92a0e57664835ee58d90c8017f0a59b9fdcd9f8602b8f82318c61f07bff4101127b141d72581dc4e9e4d','15396122'),(29,'2026-01-15 13:19:23.695098','DELIVERED',2000000,9,8,'cứ mang vào nhà','2026-01-15 13:20:36.783223','VNPAY','PAID','00','0e2dbded4e29cafe06c0e3661800982bee3a0addf197dcda951e973b5245a8770b295be457d9c1ad602026d853f7011eac4dd8e152ea2e7c3865b5f7c3e94a81','15397357'),(30,'2026-01-19 01:46:25.839680','DELIVERED',3400000,10,10,'','2026-01-19 01:47:47.430653','VNPAY','PAID','00','cf9d0cf8afd69f6514b19aa9fa12f22db1f4c989ecf5cb4855d82762857d9ced8df60b2423a85dc121559609c249bc2fc0438c2dfb1c7b3cc760af427fe104f8','15401269'),(31,'2026-01-19 09:47:15.311265','PENDING',60000000,9,8,'',NULL,'VNPAY',NULL,NULL,NULL,NULL),(32,'2026-01-19 09:47:50.423782','DELIVERED',3400000,9,8,'','2026-01-19 09:48:54.886265','VNPAY','PAID','00','ee06185e85d660efdbcef467bb7da762836f2f1fd92214a12158bc68608d277fc4e4b41a58304db2a955556f68e91d9278d0187586187ff8af8e834055b1075c','15401495'),(33,'2026-01-26 09:14:34.553933','CONFIRMED',4500000,9,8,'','2026-01-26 09:15:30.525474','VNPAY','PAID','00','8d700793b2f6537aef7c1be3e1f78a80ff2effae2c130b7680b7912c17f580dee9297f29b6b81974ee7f636d212654ec49774a8b8cc33990e0fecf7bb4665aa7','15410463');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `stock_quantity` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (2,'2025-11-15 03:56:44.232384','Trang trí','https://res.cloudinary.com/deurw9jqh/image/upload/v1763153802/products/pk1uxvjxy7fl8qtvxmob.jpg','Kệ tivi phẳng gỗ hương đá',7500000,1,98),(3,'2025-11-16 22:06:11.403972','Trang trí','https://res.cloudinary.com/deurw9jqh/image/upload/v1763305765/products/l4w1gzpk5tivqmgdifdo.jpg','Sofa gỗ xoan đào cao cấp',12500000,2,50),(5,'2025-11-27 01:48:03.826292','vòng gỗ sang trọng','https://res.cloudinary.com/deurw9jqh/image/upload/v1764182883/products/s4en9mq7dzojlugxmgrs.webp','Vòng tay gỗ hoàng đàn tuyết Lạng Sơn',3000000,5,99),(7,'2025-11-27 01:49:17.858509','mang lại tài lộc cho gia chủ','https://res.cloudinary.com/deurw9jqh/image/upload/v1764182957/products/lgkfahuofvwxcyvtrztn.jpg','Tượng phật di lạc',10000000,4,99),(8,'2025-11-27 02:12:15.423652','Vòng tay','https://res.cloudinary.com/deurw9jqh/image/upload/v1764184334/products/yf8vkyafq7xm0dsnwqp6.webp','Vòng tay gỗ Sưa đỏ',300000,3,99),(9,'2025-11-27 02:22:13.812370','bàn ghế sang trọng đẳng cấp','https://res.cloudinary.com/deurw9jqh/image/upload/v1764184933/products/ccxyjxfigl2skud5tgkw.jpg','Bộ bàn ghế Tần Thủy Hoàng',50000000,1,9),(10,'2025-11-27 02:27:19.191972','Êm ái','https://res.cloudinary.com/deurw9jqh/image/upload/v1764185238/products/mgwgappzdyj8ophctq19.webp','Giường ngủ',8500000,2,14),(11,'2025-11-27 02:32:53.148478','Trang trí','https://res.cloudinary.com/deurw9jqh/image/upload/v1764185572/products/ijlfucscf6wcnlsooqh0.webp','Tượng phật quan âm hoàng đàn tuyết',100000000,5,7),(12,'2025-12-03 15:38:28.213341','Bộ Bàn Ghế Nghê Bảo Đỉnh Gỗ Hương Đá gỗ hương 6 món đục đẹp 3D cao cấp. Gỗ đã qua tẩm sấy điện chống mối mọt, cong vênh.','https://res.cloudinary.com/deurw9jqh/image/upload/v1764751107/products/lvowh62ju9cemqgooebo.webp','Bộ Bàn Ghế Nghê Bảo Đỉnh',80000000,1,6),(13,'2025-12-03 15:42:25.728141','','https://res.cloudinary.com/deurw9jqh/image/upload/v1764751345/products/hc2ymdksbin2zahqc1gn.webp','Bàn Ghế Hoàng Gia',38000000,1,10),(14,'2025-12-03 15:57:57.710887','Tên sản phẩm : Đồng Hồ Tứ Trụ Gỗ Hương Lắp Phụ Kiện Nhôm  Cao 2m3\r\nChất Liệu: Gỗ Hương Đá\r\nKích Thước: Cao 230cm Rộng 90cm Sâu 45cm\r\nQuy Cách: Sản phẩm được làm cực kì dầy dặn và được chế tác với hồi liền, hậu liền, cột liền. Được trang bị phụ kiện đồng giả cổ và chạy máy Sunny Hàn Quốc bảo hành máy chính hàng 2 năm.  Đây là cây tứ trụ vát mẫu mới nhất thị trường và được cở sở bán cực kì chạy trong thời gian vừa qua','https://res.cloudinary.com/deurw9jqh/image/upload/v1764752277/products/ypiwnzxunv5ltgwqdkow.jpg','Đồng hồ tứ trụ',16000000,1,10),(15,'2025-12-03 16:06:14.060344','Tên sản phẩm : Bàn ăn xoan đào chữ nhật 1m6 6 ghế\r\nNhà sản xuất : Nội Thất Gỗ Xinh\r\nNhà Phân Phối : Gỗ Xinh\r\nChất liệu : Gỗ xoan đào Bắc \r\nMàu sắc : Màu cánh gián\r\nKích thước bàn : 1m6*80*75\r\nSố Lượng ghế : 6','https://res.cloudinary.com/deurw9jqh/image/upload/v1764752773/products/z30ppo0hz4nhasqzlfmj.jpg','Bàn ăn xoan đào chữ nhật 1m6 6 ghế',5800000,2,5),(16,'2025-12-03 16:08:01.074766','Kích Thước : 1m9 x cao 2m1 x sâu 60\r\nChất Liệu : Gỗ đã xử lý sấy lò hơi\r\nMàu Sắc : Màu vân gỗ tự nhiên\r\nBảo Hành - Bảo Trì: 5 năm\r\nVận chuyển lặp đặt nội thành Hà Nội miễn phí - Hỗ trợ cước đường dài và ngoại tỉnh.','https://res.cloudinary.com/deurw9jqh/image/upload/v1764752880/products/wjvejhxzbmyl9rkk4vbs.jpg','Tủ áo 4 cánh gỗ xoan đào',6900000,2,12),(18,'2025-12-03 16:16:43.333081','gỗ tự nhiên có thể xài thấm nước thoải mái mà k sợ bong tróc hay nở như ván công nghiệp','https://res.cloudinary.com/deurw9jqh/image/upload/v1764753402/products/vasr3qmcysj65yezess7.webp','Bàn thờ xoan đào',3800000,2,25),(19,'2025-12-03 16:19:05.984228','Cực kì sang trọng và đẳng cấp','https://res.cloudinary.com/deurw9jqh/image/upload/v1764753545/products/ty6ojavz9a1j0ikveb4o.jpg','Bút gỗ sưa đỏ Quảng Bình vân siêu vip',2000000,3,79),(20,'2025-12-03 16:20:06.031376','Bộ ấm chén được làm từ gỗ sưa bắc bộ cây gỗ trên 25 năm tuổi.','https://res.cloudinary.com/deurw9jqh/image/upload/v1764753605/products/m5bdewxghdsvgy8obscf.jpg','Bộ ấm chén gỗ sưa',4000000,3,36),(21,'2025-12-03 16:21:58.981181','gối gỗ sưa đỏ Quảng bình hàng vân đẹp, vân chọn lọc, nếu bạn thích chơi vân gỗ thì nó rất phù hợp để sở hữu vì nó có đủ các loại vân như chun sụn chớp xoắn lông chuột trên các hạt gối, gối gỗ sưa có tác dụng lưu thông khí huyết rất tốt cho sức khỏe \r\nhai đầu gối là gỗ cẩm, chỉ có các hạt gối chữ nhật là sưa nhé','https://res.cloudinary.com/deurw9jqh/image/upload/v1764753712/products/vo786khs70oesmrwy6vd.jpg','Gối gỗ sưa đỏ hàng vân đẹp, hai đầu gỗ cẩm',4000000,3,24),(23,'2025-12-03 16:23:13.943123','Di lặc gỗ sưa đỏ để ô tô, đồng hành cùng tiền tài may mắn xua tan mọi lo âu mệt mỏi','https://res.cloudinary.com/deurw9jqh/image/upload/v1764753793/products/litbri5shpkvgkiuscfo.jpg','Di lặc gỗ sưa đỏ',3000000,3,34),(24,'2025-12-03 16:25:48.186952','KT: cao 35 ngang 32 sâu 16\r\nGỗ trắc Việt đỏ đen rất đẹp\r\ntác phẩm để trưng trên bàn làm việc ','https://res.cloudinary.com/deurw9jqh/image/upload/v1764753947/products/thlrogc5lpzbn5qplpgn.webp','Độc mã Gỗ trắc đỏ đen gỗ trắc Việt Nam',4500000,4,51),(25,'2025-12-03 16:26:46.781980','Bộ Trúc Lam 8 món, đoản dài 1m73, sâu 50 cao 1m10 , ghế sâu 47,rộng 57 cao 107 , bàn dài 1m34 rộng 70, cao 61, đôn vuông 37 cao 44','https://res.cloudinary.com/deurw9jqh/image/upload/v1764754006/products/o1bwocobb7c6v0xwlwnl.jpg','Bộ Bàn Ghế Gỗ Trắc',60000000,4,2),(26,'2025-12-03 16:35:05.659598','Bàn cờ tướng gỗ Trắc khi gấp lại có thể để quân bên trong vô cùng tiện lợi tạo nên sự gọn gàng và hạn chế tình trạng mất quân.','https://res.cloudinary.com/deurw9jqh/image/upload/v1764754504/products/dyhldenc6dfscqc5b9mz.png','Bàn cờ tướng gỗ Trắc siêu bền đẹp',990000,4,70),(27,'2025-12-03 16:36:07.464267','Cao 10,8 Mặt 33-21 (cm)','https://res.cloudinary.com/deurw9jqh/image/upload/v1764754567/products/n40oxj72nxtasyf56taz.jpg','Khay Trà Gỗ Trắc',3500000,4,46),(28,'2025-12-03 16:37:36.312043','Trong phong thủy Ngũ hành, Lộc bình hành Mộc làm giảm bớt tác động của hành Kim và hành Thổ tạo sự cân bằng cho ngôi nhà.\r\n    Kích thước: cao 50 đường kính 16 cm\r\n Lộc bình thường có kích cỡ thon nhỏ nhưng cao. Trưng bày tiết kiệm diện tích thường được trưng tại những vị trí nhỏ, hẹp.','https://res.cloudinary.com/deurw9jqh/image/upload/v1764754655/products/rokgajd3cum6rrrivvvb.webp','Lộc Bình Gỗ Trắc Vân Tuyệt Đẹp',3400000,4,41),(29,'2025-12-03 16:45:29.791568','Kích thước tượng: Cao 35 cm, ngang 16 cm, sâu 10 cm\r\nKích thước cả đế: Cao 41 cm, ngang 26 cm, sâu 15 cm','https://res.cloudinary.com/deurw9jqh/image/upload/v1764755129/products/zdrme8zofscf1ynzwmqz.jpg','Tượng Rồng Gỗ Hoàng Đàn Tuyết',22990000,5,35),(35,NULL,'Sang trọng đẳng cấp','https://res.cloudinary.com/deurw9jqh/image/upload/v1768753085/products/k4qcrnfcnxsdqusnhys6.jpg','Tượng Phúc Lộc Thọ (Tam Đa) Gỗ Rễ Hoàng Đàn',29990000,5,7);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_tokens`
--

DROP TABLE IF EXISTS `refresh_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) DEFAULT NULL,
  `token` varchar(1000) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7tdcd6ab5wsgoudnvj7xf1b7l` (`user_id`),
  CONSTRAINT `FK1lih5y2npsf8u5o3vhdb9y0os` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES (60,'2025-12-30 20:00:39.418770','c18d96af-5130-40ba-a348-025205e7c213',1),(91,'2026-02-02 02:13:47.651520','109ad96f-62e9-41e4-9149-df2da840ec66',8);
/*!40000 ALTER TABLE `refresh_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpl51cejpw4gy5swfar8br9ngi` (`product_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpl51cejpw4gy5swfar8br9ngi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'ok','2025-11-19 02:01:57.262938',5,3,1),(2,'ok','2025-11-24 02:47:30.691233',5,2,5),(3,'1234','2025-11-24 02:47:38.944847',5,2,5),(4,'ok','2025-11-26 02:16:43.690862',5,2,1),(5,'rất đẹp, hợp phong thủy','2026-01-14 17:00:36.177475',5,28,8);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `user_role` enum('ROLE_ADMIN','ROLE_EMPLOYEE','ROLE_USER') DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK9q63snka3mdh91as4io72espi` (`phone_number`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2025-11-15 02:18:32.872472','phucchinh@gmail.com','$2a$10$cxhlKPoTdna2LvG0x3vohuH1a40GfnALooN9fL0C9JoC502hwOndi','0987654321','ROLE_EMPLOYEE','phucchinh'),(3,'2025-11-15 02:29:24.473205','admin@dogomynghe.com','$2a$10$MhY2uOTsMlJicUvk2z5iquUNNcX2jg51W5..xcS9GlGehBpoo0v7i','0899727854','ROLE_ADMIN','admin'),(4,'2025-11-21 23:43:21.449553','tuanphong@gmail.com','$2a$10$oG04cXBTxKVZkOcsk49PTOqLZ.muoXKzlo5/T6O7Hwvi8zuvi6jDO','0891483745','ROLE_EMPLOYEE','tuanphong'),(5,'2025-11-21 23:52:20.567619','huyentrinh@gmail.com','$2a$10$kEaH3pAoieqH/A5sJtBFKeOMvGRYV6TIYv6YoIMz8Bf5lH4M0Hk.e','0984567343','ROLE_USER','huyentrinh'),(6,'2025-12-23 12:16:45.064837','quangtruong@gmail.com','$2a$10$2bmuIG8cFxulsHYn6/lEyefWSPILRujUKN/CuUjB3K61iOwS.Eeca','0984568342','ROLE_USER','quangtruong'),(7,'2025-12-25 02:34:07.374019','kieunga@gmail.com','$2a$10$yogtEw49Iqc4bn5PcPf.o.AuZe/GPA2RbINpSL1yEWLpHcSe.KlW.','0984283745','ROLE_USER','kieunga'),(8,'2026-01-14 16:55:25.849146','theduyen@gmail.com','$2a$10$ViRG4v0EWK47487rdDsxueG4laKuogVDM/MwjLb1fnqATRF/S0jze','0832948213','ROLE_USER','theduyen'),(10,'2026-01-19 01:45:10.429446','thuhue@gmail.com','$2a$10$8QaHAaIzSxROysnYR.3IZ.S00.4Mj75HbTaMSUj2ejNDlA2NzjagS','0938475123','ROLE_USER','thuhue');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-04 10:14:45
