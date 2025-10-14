import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AppProvider } from './lib/app-context';
import { Header } from './components/header';
import { LandingPage } from './pages/landing';
import { DealsPage } from './pages/deals';
import { DealDetailPage } from './pages/deal-detail';
import { AuthPage } from './pages/auth';
import { CheckoutPage } from './pages/checkout';
import { OrdersPage } from './pages/orders';
import { OrderConfirmationPage } from './pages/order-confirmation';
import { Toaster } from './components/ui/sonner';

export default function App() {
  return (
    <BrowserRouter>
      <AppProvider>
        <div className="min-h-screen">
          <Header />
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/deals" element={<DealsPage />} />
            <Route path="/deal/:id" element={<DealDetailPage />} />
            <Route path="/auth" element={<AuthPage />} />
            <Route path="/checkout" element={<CheckoutPage />} />
            <Route path="/orders" element={<OrdersPage />} />
            <Route path="/order/:id" element={<OrderConfirmationPage />} />
          </Routes>
          <Toaster />
        </div>
      </AppProvider>
    </BrowserRouter>
  );
}