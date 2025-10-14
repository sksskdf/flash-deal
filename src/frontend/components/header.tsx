import { Link, useLocation } from 'react-router-dom';
import { ShoppingCart, Zap } from 'lucide-react@0.487.0';
import { Button } from './ui/button';
import { useApp } from '../lib/app-context';

export function Header() {
  const { user, cart } = useApp();
  const location = useLocation();
  const cartCount = cart.reduce((n, it) => n + it.quantity, 0);

  return (
    <header className="bg-white border-b sticky top-0 z-40" style={{ borderColor: 'var(--fd-border-default)' }}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-14 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-md bg-gradient-to-br from-[var(--fd-primary-600)] to-[var(--fd-info-600)] flex items-center justify-center">
            <Zap className="w-4 h-4 text-white" />
          </div>
          <span className="text-lg" style={{ fontWeight: 700 }}>FlashDeal</span>
        </Link>

        <nav className="flex items-center gap-2">
          <Link to="/deals">
            <Button variant={location.pathname.startsWith('/deals') ? 'default' : 'ghost'}>
              딜 둘러보기
            </Button>
          </Link>
          <Link to="/checkout" className="relative">
            <Button variant="ghost" size="icon">
              <ShoppingCart />
            </Button>
            {cartCount > 0 && (
              <span className="absolute -top-1 -right-1 text-white text-xs rounded-full px-1" style={{ backgroundColor: 'var(--fd-danger-600)' }}>
                {cartCount}
              </span>
            )}
          </Link>
          {!user ? (
            <Link to="/auth">
              <Button style={{ backgroundColor: 'var(--fd-primary-600)' }}>로그인</Button>
            </Link>
          ) : (
            <Link to="/orders">
              <Button variant="outline">내 주문</Button>
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
}

