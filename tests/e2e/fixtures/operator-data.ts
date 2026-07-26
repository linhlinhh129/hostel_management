export const VALID_INCIDENT = {
  facility: 'Ký túc xá Cầu Giấy (CG)',
  locationType: 'Khu vực chung',
  publicArea: 'Hành lang',
  category: 'Điện',
  priority: 'Khẩn cấp',
  description: 'Bóng đèn hành lang tầng 2 bị chập cháy, cần thay gấp.',
  imagePath: 'tests/e2e/fixtures/sample-image.jpg'
};

export const VALID_ROOM_INCIDENT = {
  facility: 'Ký túc xá Cầu Giấy (CG)',
  locationType: 'Phòng',
  roomId: 'CG0101',
  category: 'Nước',
  priority: 'Bình thường',
  description: 'Vòi nước bồn rửa mặt bị rỉ nước.',
  imagePath: 'tests/e2e/fixtures/sample-image.jpg'
};

export const METER_READING_DATA = {
  roomId: 'CG0101',
  newElectric: 200, // Make sure > previous (168)
  newWater: 50, // Make sure > previous (42)
  electricImage: 'tests/e2e/fixtures/sample-image.jpg',
  waterImage: 'tests/e2e/fixtures/sample-image.jpg'
};

export const METER_READING_INVALID = {
  roomId: 'CG0101',
  newElectric: 100, // < 168 (invalid)
  newWater: 30, // < 42 (invalid)
  electricImage: 'tests/e2e/fixtures/sample-image.jpg',
  waterImage: 'tests/e2e/fixtures/sample-image.jpg'
};

export const REQUEST_DETAIL_DATA = {
  requestId: '1',
  notes: 'Đã thay thế bóng đèn mới, hoạt động bình thường.',
  afterImage: 'tests/e2e/fixtures/sample-image.jpg'
};
