import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Clock, Sparkles, Loader2, Calendar, CheckCircle2 } from 'lucide-react';
import { Card, CardContent } from '../ui/card';

const STORAGE_KEY = 'waiting-screen-variant';

const VARIANTS = {
  clock: 'clock',
  progress: 'progress',
  rotating: 'rotating',
  date: 'date',
  icon: 'icon',
};

const WaitingScreen = () => {
  const [variant, setVariant] = useState(null);
  const [time, setTime] = useState(new Date());
  const [statusIndex, setStatusIndex] = useState(0);

  const statusMessages = [
    'Waiting for admin to start the test',
    'Preparing your session',
    'Almost ready',
  ];

  useEffect(() => {
    // Get or assign variant
    const storedVariant = localStorage.getItem(STORAGE_KEY);
    if (storedVariant && Object.values(VARIANTS).includes(storedVariant)) {
      setVariant(storedVariant);
    } else {
      const variants = Object.values(VARIANTS);
      const randomVariant = variants[Math.floor(Math.random() * variants.length)];
      localStorage.setItem(STORAGE_KEY, randomVariant);
      setVariant(randomVariant);
    }

    // Update time every second
    const timeInterval = setInterval(() => {
      setTime(new Date());
    }, 1000);

    // Rotate status messages
    const statusInterval = setInterval(() => {
      setStatusIndex((prev) => (prev + 1) % statusMessages.length);
    }, 3000);

    return () => {
      clearInterval(timeInterval);
      clearInterval(statusInterval);
    };
  }, []);

  if (!variant) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
      </div>
    );
  }

  // Format time in 12-hour format with AM/PM
  const formatTime12Hour = (date) => {
    let hours = date.getHours();
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12; // 0 should be 12
    return {
      time: `${hours.toString().padStart(2, '0')}:${minutes}:${seconds} ${ampm}`,
      timeShort: `${hours.toString().padStart(2, '0')}:${minutes} ${ampm}`,
    };
  };

  const timeFormatted = formatTime12Hour(time);
  const dateStr = time.toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-slate-50 flex items-center justify-center p-4">
      <Card className="w-full max-w-lg bg-white/90 backdrop-blur-sm border-slate-200 shadow-soft-lg">
        <CardContent className="p-12">
          <div className="text-center">
            {/* Icon */}
            {variant === VARIANTS.icon && (
              <motion.div
                className="mb-8 flex justify-center"
                animate={{ rotate: [0, 5, -5, 0] }}
                transition={{ duration: 4, repeat: Infinity }}
              >
                <div className="h-20 w-20 rounded-full bg-gradient-to-br from-blue-100 to-blue-200 flex items-center justify-center">
                  <CheckCircle2 className="h-10 w-10 text-blue-600" />
                </div>
              </motion.div>
            )}

            {variant === VARIANTS.clock && (
              <motion.div
                className="mb-8 flex justify-center"
                animate={{ rotate: [0, 5, -5, 0] }}
                transition={{ duration: 4, repeat: Infinity }}
              >
                <div className="h-20 w-20 rounded-full bg-gradient-to-br from-blue-100 to-blue-200 flex items-center justify-center">
                  <Clock className="h-10 w-10 text-blue-600" />
                </div>
              </motion.div>
            )}

            {variant === VARIANTS.progress && (
              <div className="mb-8 flex justify-center">
                <div className="h-20 w-20 rounded-full bg-gradient-to-br from-blue-100 to-blue-200 flex items-center justify-center">
                  <Sparkles className="h-10 w-10 text-blue-600" />
                </div>
              </div>
            )}

            {variant === VARIANTS.rotating && (
              <div className="mb-8 flex justify-center">
                <div className="h-20 w-20 rounded-full bg-gradient-to-br from-blue-100 to-blue-200 flex items-center justify-center">
                  <Loader2 className="h-10 w-10 text-blue-600 animate-spin" />
                </div>
              </div>
            )}

            {variant === VARIANTS.date && (
              <div className="mb-8 flex justify-center">
                <div className="h-20 w-20 rounded-full bg-gradient-to-br from-blue-100 to-blue-200 flex items-center justify-center">
                  <Calendar className="h-10 w-10 text-blue-600" />
                </div>
              </div>
            )}

            {/* Time Display - Clock and Progress variants */}
            {(variant === VARIANTS.clock || variant === VARIANTS.progress) && (
              <div className="mb-8">
                <motion.div
                  key={timeFormatted.time}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="font-mono text-5xl font-light text-slate-800 mb-2 tracking-wider"
                >
                  {timeFormatted.time}
                </motion.div>
                <p className="text-sm text-slate-500 font-light">{dateStr}</p>
              </div>
            )}

            {/* Date Display - Date variant */}
            {variant === VARIANTS.date && (
              <div className="mb-8">
                <p className="text-lg text-slate-600 font-light mb-2">{dateStr}</p>
                <motion.div
                  key={timeFormatted.time}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="font-mono text-2xl font-light text-slate-800 tracking-wider"
                >
                  {timeFormatted.time}
                </motion.div>
              </div>
            )}

            {/* Title */}
            <div className="mb-8">
              <h2 className="text-xl font-light text-slate-700 mb-2">Waiting to Begin</h2>
              
              {/* Rotating status message */}
              {variant === VARIANTS.rotating ? (
                <motion.p
                  key={statusIndex}
                  initial={{ opacity: 0, y: 5 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="text-sm text-slate-500 font-light"
                >
                  {statusMessages[statusIndex]}
                </motion.p>
              ) : (
                <p className="text-sm text-slate-500 font-light">
                  Waiting for admin to start the test
                </p>
              )}
            </div>

            {/* Progress Dots - Progress variant */}
            {variant === VARIANTS.progress && (
              <div className="flex items-center justify-center gap-1 mb-4">
                {Array.from({ length: 12 }, (_, i) => (
                  <motion.div
                    key={i}
                    className="h-1 w-1 rounded-full bg-blue-400"
                    animate={{
                      opacity: [0.3, 1, 0.3],
                      scale: [1, 1.2, 1],
                    }}
                    transition={{
                      duration: 1.5,
                      repeat: Infinity,
                      delay: i * 0.1,
                    }}
                  />
                ))}
              </div>
            )}

            {/* Loading Dots - Other variants */}
            {variant !== VARIANTS.progress && (
              <div className="flex items-center justify-center gap-2">
                {Array.from({ length: 3 }, (_, i) => (
                  <motion.div
                    key={i}
                    className="h-2 w-2 rounded-full bg-gray-400"
                    animate={{
                      scale: [1, 1.3, 1],
                      opacity: [0.4, 1, 0.4],
                    }}
                    transition={{
                      duration: 1.5,
                      repeat: Infinity,
                      delay: i * 0.2,
                    }}
                  />
                ))}
              </div>
            )}

            {/* Bottom Text */}
            <div className="mt-12 pt-8 border-t border-gray-100">
              <p className="text-xs text-gray-400 text-center font-light">
                Your session will begin automatically
              </p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default WaitingScreen;
