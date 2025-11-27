import { useNavigate } from 'react-router-dom';
import { mockDeals } from '../lib/mock-data';
import { DealCard } from '../components/deal-card';
import { CountdownTimer } from '../components/countdown-timer';
import { Button } from '../components/ui/button';
import { useApp } from '../lib/app-context';
import { toast } from 'sonner@2.0.3';
import { Zap, TrendingUp } from 'lucide-react';

export function LandingPage() {
  const navigate = useNavigate();
  const { addToCart, user } = useApp();

  const activeDeals = mockDeals.filter(d => d.status === 'active');
  const upcomingDeals = mockDeals.filter(d => d.status === 'upcoming');
  const nextDeal = upcomingDeals[0] || activeDeals[0];
  const featuredDeals = activeDeals.slice(0, 4);

  const handleBuyNow = (deal: any) => {
    if (!user) {
      navigate('/auth', { state: { returnTo: `/deal/${deal.id}` } });
      return;
    }
    addToCart(deal);
    toast.success(`${deal.title}을(를) 장바구니에 담았습니다!`);
  };

  return (
    <div className="min-h-screen bg-[var(--fd-bg-app)]">
      {/* Hero Section */}
      <section className="bg-gradient-to-br from-[var(--fd-primary-600)] via-[var(--fd-primary-700)] to-[var(--fd-info-600)] text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 sm:py-24">
          <div className="text-center space-y-8">
            <div className="inline-flex items-center gap-2 px-4 py-2 bg-white/10 rounded-full backdrop-blur-sm">
              <Zap className="w-4 h-4 text-yellow-300" />
              <span className="text-sm">한정 시간 특가</span>
            </div>
            
            <h1 className="text-4xl sm:text-5xl lg:text-6xl max-w-4xl mx-auto" style={{ fontWeight: 700 }}>
              최대 <span className="text-yellow-300">70% 할인</span> 플래시 딜
            </h1>
            
            <p className="text-xl text-white/90 max-w-2xl mx-auto">
              번개처럼 빠르게 사라지는 놀라운 특가를 놓치지 마세요.
              매일 새로운 딜, 한정 수량으로 제공됩니다.
            </p>

            {nextDeal && (
              <div className="max-w-2xl mx-auto">
                <CountdownTimer
                  targetTime={nextDeal.status === 'upcoming' ? nextDeal.startsAt : nextDeal.endsAt}
                  variant="banner"
                />
              </div>
            )}

            <div className="flex gap-4 justify-center flex-wrap">
              <Button 
                size="lg" 
                className="bg-white text-[var(--fd-primary-600)] hover:bg-white/90"
                onClick={() => navigate('/deals')}
              >
                <TrendingUp className="w-5 h-5 mr-2" />
                모든 딜 보기
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Deals */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h2 className="text-3xl" style={{ fontWeight: 700 }}>
              🔥 지금 핫한 딜
            </h2>
            <p className="text-[var(--fd-fg-muted)] mt-2">
              한정 수량 - 품절 전에 서두르세요!
            </p>
          </div>
          <Button 
            variant="outline" 
            onClick={() => navigate('/deals')}
            className="hidden sm:flex"
          >
            전체 보기
          </Button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {featuredDeals.map((deal) => (
            <DealCard
              key={deal.id}
              deal={deal}
              onClick={() => navigate(`/deal/${deal.id}`)}
              onBuyNow={() => handleBuyNow(deal)}
            />
          ))}
        </div>

        <div className="mt-8 text-center sm:hidden">
          <Button 
            variant="outline" 
            onClick={() => navigate('/deals')}
            className="w-full"
          >
            모든 딜 보기
          </Button>
        </div>
      </section>

      {/* Benefits Section */}
      <section className="bg-white border-t border-[var(--fd-border-default)]">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="w-16 h-16 rounded-full bg-[var(--fd-primary-50)] flex items-center justify-center mx-auto mb-4">
                <Zap className="w-8 h-8 text-[var(--fd-primary-600)]" />
              </div>
              <h3 style={{ fontWeight: 600 }}>번개 같은 딜</h3>
              <p className="text-[var(--fd-fg-muted)] mt-2">
                매일 새로운 딜이 등장합니다. 선착순 마감!
              </p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 rounded-full bg-[var(--fd-success-50)] flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-[var(--fd-success-600)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 style={{ fontWeight: 600 }}>검증된 상품</h3>
              <p className="text-[var(--fd-fg-muted)] mt-2">
                신뢰할 수 있는 판매자의 100% 정품 상품만 판매합니다.
              </p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 rounded-full bg-[var(--fd-info-50)] flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-[var(--fd-info-600)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 style={{ fontWeight: 600 }}>최저가 보장</h3>
              <p className="text-[var(--fd-fg-muted)] mt-2">
                소비자가 대비 최대 70% 할인, 확실한 절약을 보장합니다.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
