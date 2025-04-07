db = db.getSiblingDB('Longevity_Meals');  // DB 이름 설정

db.users.insertMany([
  {
    name: "홍길동",
    birthDate: ISODate("1960-05-01T00:00:00Z"),
    healthData: [
      { date: "2025-04-01", steps: 7000 },
      { date: "2025-04-02", steps: 8000 }
    ]
  },
  {
    name: "이순신",
    birthDate: ISODate("1955-10-10T00:00:00Z"),
    healthData: [
      { date: "2025-04-01", steps: 9000 }
    ]
  }
]);