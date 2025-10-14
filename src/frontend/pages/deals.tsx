import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { mockDeals, categories, DealStatus } from '../lib/mock-data';
import { DealCard } from '../components/deal-card';
import { Button } from '../components/ui/button';
import { Tabs, TabsList, TabsTrigger } from '../components/ui/tabs';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { useApp } from '../lib/app-context';
import { toast } from 'sonner@2.0.3';
import { Filter, ArrowUpDown } from 'lucide-react';

type SortOption = 'popular' | 'discount' | 'newest' | 'ending';

export function DealsPage() {
  const navigate = useNavigate();
  const { addToCart, user } = useApp();
  const [statusFilter, setStatusFilter] = useState<DealStatus | 'all'>('all');
  const [categoryFilter, setCategoryFilter] = useState('All');
  const [sortBy, setSortBy] = useState<SortOption>('popular');

  const handleBuyNow = (deal: any) => {
    if (!user) {
      navigate('/auth', { state: { returnTo: `/deal/${deal.id}` } });
      return;
    }
    addToCart(deal);
    toast.success(`${deal.title}을(를) 장바구니에 담았습니다!`);
  };

  const filteredDeals = mockDeals
    .filter(deal => {
      if (statusFilter !== 'all' && deal.status !== statusFilter) return false;
      if (categoryFilter !== 'All' && deal.category !== categoryFilter) return false;
      return true;
    })
    .sort((a, b) => {
      switch (sortBy) {
        case 'discount':
          return b.price.rate - a.price.rate;
        case 'newest':
          return b.startsAt.getTime() - a.startsAt.getTime();
        case 'ending':
          return a.endsAt.getTime() - b.endsAt.getTime();
        default:
          return 0;
      }
    });

  return (
    <div className="min-h-screen bg-[var(--fd-bg-app)]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl sm:text-4xl mb-2" style={{ fontWeight: 700 }}>
            모든 플래시 딜
          </h1>
          <p className="text-[var(--fd-fg-muted)]">
            {filteredDeals.length}개의 딜이 있습니다
          </p>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-xl p-4 sm:p-6 mb-8" style={{ boxShadow: 'var(--fd-shadow-sm)' }}>
          <div className="space-y-4">
            {/* Status Tabs */}
            <div>
              <label className="text-sm text-[var(--fd-fg-muted)] mb-2 flex items-center gap-2">
                <Filter className="w-4 h-4" />
                상태
              </label>
              <Tabs value={statusFilter} onValueChange={(v) => setStatusFilter(v as any)}>
                <TabsList className="grid w-full grid-cols-5 h-auto">
                  <TabsTrigger value="all">전체</TabsTrigger>
                  <TabsTrigger value="active">진행중</TabsTrigger>
                  <TabsTrigger value="upcoming">예정</TabsTrigger>
                  <TabsTrigger value="soldout">품절</TabsTrigger>
                  <TabsTrigger value="ended">종료</TabsTrigger>
                </TabsList>
              </Tabs>
            </div>

            {/* Category & Sort */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="text-sm text-[var(--fd-fg-muted)] mb-2 block">
                  카테고리
                </label>
                <Select value={categoryFilter} onValueChange={setCategoryFilter}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {categories.map(cat => (
                      <SelectItem key={cat} value={cat}>{cat}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div>
                <label className="text-sm text-[var(--fd-fg-muted)] mb-2 flex items-center gap-2">
                  <ArrowUpDown className="w-4 h-4" />
                  정렬
                </label>
                <Select value={sortBy} onValueChange={(v) => setSortBy(v as SortOption)}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="popular">인기순</SelectItem>
                    <SelectItem value="discount">높은 할인율</SelectItem>
                    <SelectItem value="newest">최신순</SelectItem>
                    <SelectItem value="ending">마감임박</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </div>
        </div>

        {/* Deals Grid */}
        {filteredDeals.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredDeals.map((deal) => (
              <DealCard
                key={deal.id}
                deal={deal}
                onClick={() => navigate(`/deal/${deal.id}`)}
                onBuyNow={() => handleBuyNow(deal)}
              />
            ))}
          </div>
        ) : (
          <div className="text-center py-16">
            <div className="w-24 h-24 rounded-full bg-[var(--fd-gray-100)] flex items-center justify-center mx-auto mb-4">
              <Filter className="w-12 h-12 text-[var(--fd-fg-muted)]" />
            </div>
            <h3 className="text-xl mb-2" style={{ fontWeight: 600 }}>
              딜을 찾을 수 없습니다
            </h3>
            <p className="text-[var(--fd-fg-muted)] mb-6">
              필터를 조정하여 더 많은 결과를 확인하세요
            </p>
            <Button 
              onClick={() => {
                setStatusFilter('all');
                setCategoryFilter('All');
              }}
            >
              필터 초기화
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}
