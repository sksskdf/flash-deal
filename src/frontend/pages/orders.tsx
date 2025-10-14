import { useNavigate } from 'react-router-dom';
import { useApp } from '../lib/app-context';
import { Card } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Package, ShoppingBag } from 'lucide-react';
import { ImageWithFallback } from '../components/image-with-fallback';

export function OrdersPage() {
  const navigate = useNavigate();
  const { orders, user } = useApp();

  if (!user) {
    navigate('/auth', { state: { returnTo: '/orders' } });
    return null;
  }

  const statusConfig = {
    processing: {
      variant: 'secondary' as const,
      text: '처리중',
      color: 'var(--fd-warning-600)',
    },
    confirmed: {
      variant: 'default' as const,
      text: '확인됨',
      color: 'var(--fd-success-600)',
    },
    failed: {
      variant: 'destructive' as const,
      text: '실패',
      color: 'var(--fd-danger-600)',
    },
  };

  if (orders.length === 0) {
    return (
      <div className="min-h-screen bg-[var(--fd-bg-app)] flex items-center justify-center">
        <div className="text-center">
          <div className="w-24 h-24 rounded-full bg-[var(--fd-gray-100)] flex items-center justify-center mx-auto mb-4">
            <ShoppingBag className="w-12 h-12 text-[var(--fd-fg-muted)]" />
          </div>
          <h2 className="text-2xl mb-2" style={{ fontWeight: 600 }}>
            주문 내역이 없습니다
          </h2>
          <p className="text-[var(--fd-fg-muted)] mb-6">
            쇼핑을 시작해보세요!
          </p>
          <Button onClick={() => navigate('/deals')}>
            딜 둘러보기
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[var(--fd-bg-app)]">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl mb-8" style={{ fontWeight: 700 }}>
          내 주문
        </h1>

        <div className="space-y-4">
          {orders.map((order) => {
            const status = statusConfig[order.status];
            return (
              <Card key={order.id} className="p-6">
                {/* Order Header */}
                <div className="flex flex-wrap items-start justify-between gap-4 mb-4">
                  <div>
                    <div className="flex items-center gap-3 mb-1">
                      <h3 style={{ fontWeight: 600 }}>주문 #{order.id}</h3>
                      <Badge variant={status.variant}>{status.text}</Badge>
                    </div>
                    <p className="text-sm text-[var(--fd-fg-muted)]">
                      {order.createdAt.toLocaleDateString('ko-KR', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-[var(--fd-fg-muted)]">합계</p>
                    <p className="text-2xl text-[var(--fd-primary-600)]" style={{ fontWeight: 700 }}>
                      ${order.total.toFixed(2)}
                    </p>
                  </div>
                </div>

                {/* Order Items */}
                <div className="space-y-3">
                  {order.items.map((item) => (
                    <div
                      key={item.id}
                      className="flex gap-4 p-3 rounded-lg bg-[var(--fd-bg-subtle)]"
                    >
                      <div className="w-20 h-20 rounded-lg overflow-hidden bg-white flex-shrink-0">
                        <ImageWithFallback
                          src={item.image}
                          alt={item.title}
                          className="w-full h-full object-cover"
                        />
                      </div>
                      <div className="flex-1 min-w-0">
                        <p style={{ fontWeight: 500 }} className="line-clamp-1">
                          {item.title}
                        </p>
                        <p className="text-sm text-[var(--fd-fg-muted)]">
                          수량: {item.quantity}
                        </p>
                        <p className="text-sm" style={{ fontWeight: 600 }}>
                          ${item.price.sale.toFixed(2)} 개당
                        </p>
                      </div>
                      <div className="text-right flex-shrink-0">
                        <p style={{ fontWeight: 600 }}>
                          ${(item.price.sale * item.quantity).toFixed(2)}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Status Message */}
                {order.status === 'processing' && (
                  <div className="mt-4 p-3 bg-[var(--fd-warning-50)] rounded-lg border border-[var(--fd-warning-200)] flex items-start gap-3">
                    <Package className="w-5 h-5 text-[var(--fd-warning-600)] flex-shrink-0 mt-0.5" />
                    <div>
                      <p className="text-sm" style={{ fontWeight: 500 }}>
                        주문 처리 중
                      </p>
                      <p className="text-sm text-[var(--fd-fg-muted)]">
                        배송을 준비하고 있습니다.
                      </p>
                    </div>
                  </div>
                )}

                {order.status === 'confirmed' && (
                  <div className="mt-4 p-3 bg-[var(--fd-success-50)] rounded-lg border border-[var(--fd-success-200)] flex items-start gap-3">
                    <Package className="w-5 h-5 text-[var(--fd-success-600)] flex-shrink-0 mt-0.5" />
                    <div>
                      <p className="text-sm" style={{ fontWeight: 500 }}>
                        주문 확인됨
                      </p>
                      <p className="text-sm text-[var(--fd-fg-muted)]">
                        주문이 확인되었으며 곧 배송됩니다.
                      </p>
                    </div>
                  </div>
                )}
              </Card>
            );
          })}
        </div>
      </div>
    </div>
  );
}
