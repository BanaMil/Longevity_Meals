// lib/models/address_item.dart

class AddressItem {
  final String addressRoad;
  final String? addressJibun;
  final String? postCode;
  final String? addressDetail;
  final bool? isDefault;

  AddressItem({
    required this.addressRoad,
    this.addressJibun,
    this.postCode,
    this.addressDetail,
    this.isDefault,
  });

  factory AddressItem.fromJson(Map<String, dynamic> map) => AddressItem(
        addressRoad: map['addressRoad'] as String? ?? '',
        addressJibun: map['addressJibun'] as String?,
        postCode: map['postCode'] as String?,
        addressDetail: map['addressDetail'] ?? map['detailAddress'],
        isDefault: map['isDefault'] as bool?,
      );

  Map<String, dynamic> toJson() => {
        'addressRoad': addressRoad,
        if (addressJibun != null) 'addressJibun': addressJibun,
        if (postCode != null) 'postCode': postCode,
        if (addressDetail != null) 'addressDetail': addressDetail,
        if (isDefault != null) 'isDefault': isDefault,
      };
}
