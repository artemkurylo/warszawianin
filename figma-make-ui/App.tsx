import { useState } from 'react';
import { HomeScreen } from './components/HomeScreen';
import { CameraScreen } from './components/CameraScreen';
import { PreviewScreen } from './components/PreviewScreen';
import { HistoryScreen } from './components/HistoryScreen';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<'home' | 'camera' | 'preview' | 'history'>('home');
  const [hasPhoto, setHasPhoto] = useState(false);

  return (
    <div className="size-full flex items-center justify-center bg-background">
      {/* Mobile Container */}
      <div className="w-full max-w-md h-full bg-background shadow-2xl overflow-hidden">
        {currentScreen === 'home' && <HomeScreen onNavigate={setCurrentScreen} />}
        {currentScreen === 'camera' && (
          <CameraScreen onNavigate={setCurrentScreen} onPhotoCapture={setHasPhoto} />
        )}
        {currentScreen === 'preview' && <PreviewScreen onNavigate={setCurrentScreen} />}
        {currentScreen === 'history' && <HistoryScreen onNavigate={setCurrentScreen} />}
      </div>
    </div>
  );
}