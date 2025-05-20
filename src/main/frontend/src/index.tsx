// src/index.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './App.css';

import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './styles/theme';  // путь к твоему файлу с темой

const rootElement = document.getElementById('root') as HTMLElement;
const root = ReactDOM.createRoot(rootElement);

root.render(
    <React.StrictMode>
        <ThemeProvider theme={theme}>
            <CssBaseline /> {/* Сброс стилей и базовые настройки */}
            <App />
        </ThemeProvider>
    </React.StrictMode>
);
