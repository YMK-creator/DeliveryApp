import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    palette: {
        primary: {
            main: '#FF3B30', // ярко-красный — основной цвет Яндекс.Еды
            contrastText: '#fff',
        },
        secondary: {
            main: '#FF6F00', // акцентный оранжевый
        },
        background: {
            default: '#F9F9F9', // светлый фон
            paper: '#fff',       // цвет «бумаги» (карточек, модалок)
        },
        text: {
            primary: '#2E2E2E', // тёмно-серый текст
            secondary: '#666',  // дополнительный серый
        },
    },
    typography: {
        fontFamily: '"Inter", "Roboto", "Arial", sans-serif',
        h4: {
            fontWeight: 700,
            color: '#2E2E2E',
        },
        button: {
            textTransform: 'none', // чтобы кнопки не были заглавными, как на Яндекс.Еде
            fontWeight: 600,
        },
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: '8px', // чуть более закруглённые кнопки
                },
            },
        },
        MuiAppBar: {
            styleOverrides: {
                colorPrimary: {
                    backgroundColor: '#FFD700', // жёлтый
                    color: '#2E2E2E', // тёмный цвет текста, чтобы был контраст
                    boxShadow: 'none',
                    borderBottom: '1px solid #eee',
                },
            },
        },
    },
});

export default theme;
