//models/user.dart

import 'package:frontend/models/address_item.dart'; 
class User {
  final String userid;
  final String username;

  final String? password;
  final DateTime? birthdate;
  final String? phone;

  // 보기용 전체 주소 (도로명 + 상세주소)
  final String? address;
  final String? addressRoad;
  final String? addressJibun;
  final String? postCode;
  final String? addressDetail;


  final String? token;
  final bool healthInfoSubmitted;
  final List<AddressItem> addresses; // 주소 목록

  User({
    required this.userid,
    required this.username,
    this.password,
    this.birthdate,
    this.phone,
    this.address,
    this.addressRoad,
    this.addressJibun,
    this.postCode,
    this.addressDetail,
    this.token,
    this.healthInfoSubmitted = false,
    this.addresses = const [],
  });

  factory User.fromJson(Map<String, dynamic> json) {
    final detail = (json['addressDetail'] ?? json['detailAddress']) as String?;

    // 주소 목록 파싱
    final List<AddressItem> addrList = (json['addresses'] as List<dynamic>?)
            ?.whereType<Map<String, dynamic>>()
            .map(AddressItem.fromJson)
            .toList() ??
        const [];

    return User(
      userid: (json['userid'] ?? '') as String,
      username: (json['username'] ?? '') as String,

      password: json['password'] as String?,
      birthdate: json['birthdate'] != null
          ? DateTime.tryParse(json['birthdate'] as String)
          : null,
      phone: json['phone'] as String?,

      address: json['address'] as String?,              
      addressRoad: json['addressRoad'] as String?,
      addressJibun: json['addressJibun'] as String?,
      postCode: json['postCode'] as String?,
      addressDetail: detail,

      token: json['token'] as String?,
      healthInfoSubmitted: (json['healthInfoSubmitted'] as bool?) ?? false, 
      addresses: addrList,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userid': userid,
      'username': username,
      if (password != null) 'password': password,
      if (birthdate != null) 'birthdate': birthdate!.toIso8601String(),
      if (phone != null) 'phone': phone,
      if (address != null) 'address': address,
      if (addressRoad != null) 'addressRoad': addressRoad,
      if (addressJibun != null) 'addressJibun': addressJibun,
      if (postCode != null) 'postCode': postCode,
      if (addressDetail != null) 'addressDetail': addressDetail,
      if (token != null) 'token': token,
      'healthInfoSubmitted': healthInfoSubmitted,
      if (addresses.isNotEmpty)
        'addresses': addresses.map((e) => e.toJson()).toList(),
    };
  }
}