-- 국가 데이터
INSERT INTO country (country_id, country_code, country_name) VALUES 
(1, 'KR', '대한민국');

-- 시도 데이터
INSERT INTO state (state_id, state_code, state_name, country_id) VALUES 
(1, 11, '서울특별시', 1),
(2, 26, '부산광역시', 1),
(3, 39, '제주특별자치도', 1);

-- 시군구 데이터
INSERT INTO city (city_id, city_code, city_name, state_id) VALUES 
(1, 110, '종로구', 1),
(2, 140, '중구', 1),
(3, 680, '강남구', 1),
(4, 260, '해운대구', 2),
(5, 390, '서귀포시', 3);

-- 여행지 카테고리 데이터
INSERT INTO attraction_category (attraction_category_id, attraction_category_name) VALUES 
(12, '역사관광지'),
(14, '문화시설'),
(15, '자연관광지'),
(28, '레포츠');

-- 여행지 데이터
INSERT INTO attraction (attraction_id, content_id, attraction_name, first_image1, first_image2, map_level, latitude, longitude, tel, addr1, addr2, homepage, overview, country_id, state_id, city_id, attraction_category_id) VALUES 
(1, 123456, '경복궁', 'https://example.com/gyeongbokgung.jpg', NULL, 6, 37.579617, 126.977041, '02-3700-3900', '서울특별시 종로구 사직로 161', NULL, 'http://www.royalpalace.go.kr', '조선시대 대표적인 궁궐로 1395년에 창건되었다.', 1, 1, 1, 12),
(2, 234567, '남산타워', 'https://example.com/namsan.jpg', NULL, 6, 37.551250, 126.988219, '02-3455-9277', '서울특별시 중구 남산공원길 105', NULL, 'http://www.nseoultower.co.kr', '서울 시내 전경을 한눈에 볼 수 있는 명소', 1, 1, 2, 14),
(3, 345678, '성산일출봉', 'https://example.com/seongsan.jpg', NULL, 7, 33.458056, 126.942222, '064-783-0959', '제주특별자치도 서귀포시 성산읍 일출로 284-12', NULL, 'http://www.jeju.go.kr', '제주도 동쪽 끝에 있는 세계자연유산으로 일출 명소', 1, 3, 5, 15),
(4, 456789, '해운대 해수욕장', 'https://example.com/haeundae.jpg', NULL, 8, 35.158889, 129.160833, '051-749-7601', '부산광역시 해운대구 해운대해변로 264', NULL, 'http://www.haeundae.go.kr', '부산의 대표적인 해수욕장으로 넓은 백사장과 아름다운 해안선이 특징', 1, 2, 4, 15);

-- 사용자, 리뷰, 좋아요 데이터는 생략
-- 실제 DB의 테이블 이름과 구조에 맞게 추가해야 함
