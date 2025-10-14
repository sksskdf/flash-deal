import { createContext, useContext, useState, ReactNode } from 'react';
import { Deal } from './mock-data';

interface CartItem extends Deal {
  quantity: number;
}

interface User {
  id: string;
  name: string;
  email: string;
}

interface Order {
  id: string;
  items: CartItem[];
  total: number;
  status: 'processing' | 'confirmed' | 'failed';
  createdAt: Date;
}

interface AppContextType {
  user: User | null;
  login: (email: string, password: string) => void;
  logout: () => void;
  signup: (name: string, email: string, password: string) => void;
  cart: CartItem[];
  addToCart: (deal: Deal) => void;
  removeFromCart: (dealId: string) => void;
  clearCart: () => void;
  orders: Order[];
  placeOrder: (items: CartItem[]) => Order;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export function AppProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);

  const login = (email: string, password: string) => {
    // Mock login
    setUser({
      id: '1',
      name: 'John Doe',
      email,
    });
  };

  const logout = () => {
    setUser(null);
    setCart([]);
  };

  const signup = (name: string, email: string, password: string) => {
    // Mock signup
    setUser({
      id: '1',
      name,
      email,
    });
  };

  const addToCart = (deal: Deal) => {
    setCart(prev => {
      const existing = prev.find(item => item.id === deal.id);
      if (existing) {
        return prev.map(item =>
          item.id === deal.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      }
      return [...prev, { ...deal, quantity: 1 }];
    });
  };

  const removeFromCart = (dealId: string) => {
    setCart(prev => prev.filter(item => item.id !== dealId));
  };

  const clearCart = () => {
    setCart([]);
  };

  const placeOrder = (items: CartItem[]): Order => {
    const total = items.reduce((sum, item) => sum + item.price.sale * item.quantity, 0);
    const order: Order = {
      id: `order-${Date.now()}`,
      items,
      total,
      status: 'processing',
      createdAt: new Date(),
    };
    
    // Simulate order processing
    setTimeout(() => {
      setOrders(prev => 
        prev.map(o => o.id === order.id ? { ...o, status: 'confirmed' as const } : o)
      );
    }, 2000);

    setOrders(prev => [...prev, order]);
    return order;
  };

  return (
    <AppContext.Provider
      value={{
        user,
        login,
        logout,
        signup,
        cart,
        addToCart,
        removeFromCart,
        clearCart,
        orders,
        placeOrder,
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within AppProvider');
  }
  return context;
}
