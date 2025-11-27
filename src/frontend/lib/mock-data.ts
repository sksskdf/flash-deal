export type DealStatus = 'upcoming' | 'active' | 'soldout' | 'ended';
export type InventoryLevel = 'high' | 'mid' | 'low';

export interface Deal {
  id: string;
  title: string;
  subtitle?: string;
  image: string;
  price: {
    original: number;
    sale: number;
    rate: number;
  };
  status: DealStatus;
  startsAt: Date;
  endsAt: Date;
  inventoryLevel: InventoryLevel;
  category: string;
  description?: string;
  specs?: string[];
}

const now = new Date();

export const mockDeals: Deal[] = [
  {
    id: '1',
    title: '무선 노이즈 캔슬링 헤드폰',
    subtitle: '프리미엄 오디오 경험',
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80',
    price: {
      original: 299,
      sale: 89,
      rate: 70,
    },
    status: 'active',
    startsAt: new Date(now.getTime() - 2 * 60 * 60 * 1000),
    endsAt: new Date(now.getTime() + 3 * 60 * 60 * 1000),
    inventoryLevel: 'low',
    category: '전자기기',
    description: '액티브 노이즈 캔슬링과 30시간 배터리 수명, 편안한 오버이어 디자인으로 프리미엄 사운드를 경험하세요.',
    specs: ['액티브 노이즈 캔슬링', '30시간 배터리', 'Bluetooth 5.0', '접이식 디자인'],
  },
  {
    id: '2',
    title: '4K 스마트 TV 55"',
    subtitle: '울트라 HD 엔터테인먼트',
    image: 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&q=80',
    price: {
      original: 799,
      sale: 449,
      rate: 44,
    },
    status: 'active',
    startsAt: new Date(now.getTime() - 1 * 60 * 60 * 1000),
    endsAt: new Date(now.getTime() + 5 * 60 * 60 * 1000),
    inventoryLevel: 'mid',
    category: '전자기기',
    description: 'HDR 지원, 스마트 기능, 몰입형 사운드를 갖춘 놀라운 4K UHD 디스플레이.',
    specs: ['55" 4K UHD 디스플레이', 'HDR10+', '스마트 TV 플랫폼', '음성 제어'],
  },
  {
    id: '3',
    title: '기계식 게이밍 키보드',
    subtitle: 'RGB 백라이트, Cherry MX 스위치',
    image: 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80',
    price: {
      original: 159,
      sale: 79,
      rate: 50,
    },
    status: 'active',
    startsAt: new Date(now.getTime() - 30 * 60 * 1000),
    endsAt: new Date(now.getTime() + 2 * 60 * 60 * 1000),
    inventoryLevel: 'high',
    category: '게이밍',
    description: 'Cherry MX 스위치와 커스터마이징 가능한 RGB 조명이 장착된 프로페셔널 게이밍 키보드.',
    specs: ['Cherry MX 스위치', 'RGB 백라이트', '알루미늄 프레임', '프로그래밍 가능한 키'],
  },
  {
    id: '4',
    title: '스마트워치 프로 시리즈 7',
    subtitle: '건강 & 피트니스 트래커',
    image: 'https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=800&q=80',
    price: {
      original: 399,
      sale: 249,
      rate: 38,
    },
    status: 'upcoming',
    startsAt: new Date(now.getTime() + 2 * 60 * 60 * 1000),
    endsAt: new Date(now.getTime() + 8 * 60 * 60 * 1000),
    inventoryLevel: 'high',
    category: '웨어러블',
    description: '고급 건강 모니터링, GPS, 방수 기능, 하루 종일 사용 가능한 배터리.',
    specs: ['심박수 모니터', 'GPS 추적', '방수 기능', '2일 배터리'],
  },
  {
    id: '5',
    title: '프리미엄 커피 메이커',
    subtitle: '집에서 즐기는 바리스타급 커피',
    image: 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=800&q=80',
    price: {
      original: 249,
      sale: 149,
      rate: 40,
    },
    status: 'soldout',
    startsAt: new Date(now.getTime() - 4 * 60 * 60 * 1000),
    endsAt: new Date(now.getTime() + 1 * 60 * 60 * 1000),
    inventoryLevel: 'low',
    category: '홈',
    description: '우유 거품기와 프로그래밍 가능한 설정을 갖춘 전문가급 에스프레소 머신.',
    specs: ['15바 압력', '우유 거품기', '프로그래밍 가능', '스테인리스 스틸'],
  },
  {
    id: '6',
    title: 'Wireless Gaming Mouse',
    subtitle: 'Ultra-Lightweight Design',
    image: 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&q=80',
    price: {
      original: 129,
      sale: 69,
      rate: 47,
    },
    status: 'ended',
    startsAt: new Date(now.getTime() - 10 * 60 * 60 * 1000),
    endsAt: new Date(now.getTime() - 1 * 60 * 60 * 1000),
    inventoryLevel: 'mid',
    category: 'Gaming',
    description: 'High-precision wireless gaming mouse with customizable DPI and RGB lighting.',
    specs: ['20,000 DPI', 'Wireless', '70g weight', 'RGB Lighting'],
  },
  {
    id: '7',
    title: 'Portable Bluetooth Speaker',
    subtitle: '360° Sound, Waterproof',
    image: 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=800&q=80',
    price: {
      original: 149,
      sale: 79,
      rate: 47,
    },
    status: 'active',
    startsAt: new Date(now.getTime() - 1 * 60 * 60 * 1000),
    endsAt: new Date(now.getTime() + 4 * 60 * 60 * 1000),
    inventoryLevel: 'mid',
    category: 'Audio',
    description: 'Powerful portable speaker with 360° sound, waterproof design, and 12-hour battery.',
    specs: ['360° Sound', 'IPX7 Waterproof', '12-hour battery', 'Bluetooth 5.0'],
  },
  {
    id: '8',
    title: 'Professional Drone with 4K Camera',
    subtitle: 'Aerial Photography Made Easy',
    image: 'https://images.unsplash.com/photo-1473968512647-3e447244af8f?w=800&q=80',
    price: {
      original: 899,
      sale: 549,
      rate: 39,
    },
    status: 'upcoming',
    startsAt: new Date(now.getTime() + 1 * 60 * 60 * 1000),
    endsAt: new Date(now.getTime() + 6 * 60 * 60 * 1000),
    inventoryLevel: 'high',
    category: 'Photography',
    description: 'Advanced drone with 4K camera, GPS, obstacle avoidance, and 25-minute flight time.',
    specs: ['4K Camera', 'GPS Navigation', 'Obstacle Avoidance', '25min flight time'],
  },
];

export const categories = ['전체', '전자기기', '게이밍', '웨어러블', '홈', '오디오', '사진'];

export const getDealById = (id: string) => mockDeals.find(deal => deal.id === id);

export const getDealsByStatus = (status: DealStatus) => mockDeals.filter(deal => deal.status === status);

export const getDealsByCategory = (category: string) => 
  category === 'All' ? mockDeals : mockDeals.filter(deal => deal.category === category);
