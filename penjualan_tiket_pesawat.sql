-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for penjualan_tiket_pesawat
CREATE DATABASE IF NOT EXISTS `penjualan_tiket_pesawat` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `penjualan_tiket_pesawat`;

-- Dumping structure for table penjualan_tiket_pesawat.bagasi
CREATE TABLE IF NOT EXISTS `bagasi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nama_penumpang` varchar(255) NOT NULL,
  `kelas` enum('Ekonomi','Bisnis') NOT NULL,
  `maskapai` varchar(255) NOT NULL,
  `berat_bawaan` decimal(5,2) NOT NULL,
  `layanan_bagasi` varchar(255) DEFAULT NULL,
  `harga_bagasi` decimal(10,2) NOT NULL,
  `kode_promo` varchar(50) DEFAULT NULL,
  `tgl_transaksi` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tgl_transaksi` (`tgl_transaksi`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table penjualan_tiket_pesawat.bagasi: ~15 rows (approximately)
INSERT INTO `bagasi` (`id`, `nama_penumpang`, `kelas`, `maskapai`, `berat_bawaan`, `layanan_bagasi`, `harga_bagasi`, `kode_promo`, `tgl_transaksi`) VALUES
	(1, 'Udin', 'Bisnis', 'Citilink', 12.00, 'Gold', 600000.00, NULL, '2024-12-12'),
	(2, 'Udin', 'Bisnis', 'Garuda Indonesia', 12.00, 'Bronze', 120000.00, 'PROMO10', '2024-12-13'),
	(3, 'Syafiq', 'Bisnis', 'Singapore Airlines', 20.00, 'Gold', 400000.00, 'PROMO10', '2024-12-21'),
	(6, 'Yusuf Syarif', 'Ekonomi', 'Citilink', 12.00, 'Gold', 240000.00, 'PROMO10', '2024-12-21'),
	(7, 'Muhammad', 'Bisnis', 'Singapore Airlines', 12.00, 'Gold', 240000.00, 'PROMO20', '2024-12-14'),
	(8, 'Muhammad', 'Bisnis', 'Singapore Airlines', 12.00, 'Gold', 240000.00, 'PROMO21', '2024-12-14'),
	(9, 'Muhammad', 'Bisnis', 'Singapore Airlines', 12.00, 'Gold', 240000.00, 'PROMO10', '2024-12-14'),
	(10, 'Udin', 'Bisnis', 'Citilink', 20.00, 'Gold', 400000.00, 'PROMO10', '2024-12-07'),
	(11, 'Syarif', 'Bisnis', 'Garuda Indonesia', 20.00, 'Gold', 400000.00, 'PROMO10', '2024-12-19'),
	(12, 'Fika', 'Bisnis', 'Singapore Airlines', 12.00, 'Silver', 180000.00, 'PROMO10', '2024-12-21'),
	(13, 'Udin', 'Bisnis', 'Singapore Airlines', 12.00, 'Bronze', 120000.00, 'PROMO10', '2024-12-13'),
	(15, 'mamah', 'Bisnis', 'Citilink', 30.00, 'Gold', 600000.00, NULL, '2024-12-14'),
	(17, 'df', 'Ekonomi', 'Lion Air', 12.00, 'Gold', 240000.00, NULL, '2024-12-14'),
	(18, 'Tau tau', 'Ekonomi', 'Garuda Indonesia', 12.00, 'Bronze', 120000.00, NULL, '2024-12-13'),
	(19, 'Mahmud', 'Ekonomi', 'Garuda Indonesia', 12.00, 'Bronze', 120000.00, NULL, '2024-12-28'),
	(20, 'Udin', 'Bisnis', 'Singapore Airlines', 12.00, 'Bronze', 120000.00, 'PROMO20', '2024-12-13'),
	(21, 'Muhammad Hatta', 'Bisnis', 'Citilink', 10.00, 'Gold', 200000.00, 'PROMO10', '2025-01-02'),
	(22, 'Syukran', 'Ekonomi', 'Garuda Indonesia', 12.00, 'Silver', 162000.00, 'PROMO10', '2025-01-03'),
	(23, 'Ramlan', 'Ekonomi', 'Singapore Airlines', 12.00, 'Gold', 216000.00, 'PROMO10', '2025-01-04'),
	(24, 'Pilana', 'Ekonomi', 'Citilink', 12.00, 'Gold', 192000.00, 'PROMO20', '2025-01-11');

-- Dumping structure for table penjualan_tiket_pesawat.manajemen_data
CREATE TABLE IF NOT EXISTS `manajemen_data` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nama_penumpang` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `kelas` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `maskapai` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `layanan_bagasi` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `harga_tiket` decimal(10,2) NOT NULL,
  `tanggal_transaksi` date NOT NULL,
  `tanggal_pesan` date NOT NULL,
  `tanggal_keberangkatan` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table penjualan_tiket_pesawat.manajemen_data: ~3 rows (approximately)
INSERT INTO `manajemen_data` (`id`, `nama_penumpang`, `kelas`, `maskapai`, `layanan_bagasi`, `harga_tiket`, `tanggal_transaksi`, `tanggal_pesan`, `tanggal_keberangkatan`) VALUES
	(6, 'Mahmud S', 'Ekonomi', 'Citilink', 'Silver', 2500000.00, '2024-12-26', '2024-12-26', '2024-12-26'),
	(7, 'Udin Noor', 'Ekonomi', 'Singapore Airlines', 'Gold', 570000.00, '2024-12-11', '2024-12-11', '2024-12-27'),
	(8, 'Syafiq', 'Ekonomi', 'Singapore Airlines', 'Gold', 760000.00, '2024-12-05', '2024-12-05', '2024-12-27');

-- Dumping structure for table penjualan_tiket_pesawat.pemesanan
CREATE TABLE IF NOT EXISTS `pemesanan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nama_penumpang` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `kelas` enum('Ekonomi','Bisnis','First Class') COLLATE utf8mb4_general_ci NOT NULL,
  `maskapai` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `tanggal_pesan` date NOT NULL,
  `tanggal_keberangkatan` date NOT NULL,
  `harga_tiket` decimal(10,2) NOT NULL,
  `kode_promo` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table penjualan_tiket_pesawat.pemesanan: ~2 rows (approximately)
INSERT INTO `pemesanan` (`id`, `nama_penumpang`, `kelas`, `maskapai`, `tanggal_pesan`, `tanggal_keberangkatan`, `harga_tiket`, `kode_promo`) VALUES
	(3, 'Muhammad', 'Bisnis', 'Citilink', '2024-12-05', '2024-12-21', 250000.00, NULL),
	(4, 'Muhammad', 'Bisnis', 'Singapore Airlines', '2024-12-04', '2024-12-31', 546000.00, NULL),
	(5, 'Rahmi', 'Ekonomi', 'Garuda Indonesia', '2025-01-02', '2025-01-25', 168000.00, NULL),
	(6, 'Muhammad Harris Jaya', 'Bisnis', 'Garuda Indonesia', '2025-01-02', '2025-01-11', 392000.00, NULL);

-- Dumping structure for table penjualan_tiket_pesawat.penumpang
CREATE TABLE IF NOT EXISTS `penumpang` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nomor_registrasi` varchar(10) NOT NULL DEFAULT '',
  `nama_penumpang` varchar(50) NOT NULL DEFAULT '',
  `alamat` text NOT NULL,
  `no_telp` varchar(50) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table penjualan_tiket_pesawat.penumpang: ~0 rows (approximately)
INSERT INTO `penumpang` (`id`, `nomor_registrasi`, `nama_penumpang`, `alamat`, `no_telp`) VALUES
	(5, 'YABE4354', 'Muhammad Syafiq', 'Jl Salak', '089344445555'),
	(6, '7230FDFV', 'Muhammad Bisma', 'Jl Kelapa', '089233334444');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
