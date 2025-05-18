import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import {
    AppBar, Toolbar, IconButton, Typography, Drawer, List, ListItem, Button,
    Box, Paper, Badge
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

const drawerWidth = 240;

function App() {
    const [open, setOpen] = useState(false);

    const toggleDrawer = () => setOpen(prev => !prev);

    return (
        <Router>
            <Box sx={{ display: 'flex', fontFamily: 'Roboto, sans-serif' }}>
                {/* Верхняя панель */}
                <AppBar
                    position="fixed"
                    sx={{ backgroundColor: '#FFD100', color: '#000', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                >
                    <Toolbar>
                        <IconButton
                            color="inherit"
                            edge="start"
                            onClick={toggleDrawer}
                            sx={{ mr: 2 }}
                        >
                            <MenuIcon />
                        </IconButton>
                        <Typography variant="h6" sx={{ flexGrow: 1 }}>
                            🍽️ Доставка еды
                        </Typography>
                        <IconButton color="inherit">
                            <Badge badgeContent={2} color="error">
                                <ShoppingCartIcon />
                            </Badge>
                        </IconButton>
                    </Toolbar>
                </AppBar>

                {/* Боковое меню */}
                <Drawer
                    variant="temporary"
                    open={open}
                    onClose={toggleDrawer}
                    sx={{
                        '& .MuiDrawer-paper': {
                            width: drawerWidth,
                            boxSizing: 'border-box',
                        },
                    }}
                >
                    <Toolbar />
                    <List>
                        <ListItem>
                            <Button component={Link} to="/" fullWidth>🏠 Главная</Button>
                        </ListItem>
                        <ListItem>
                            <Button component={Link} to="/food" fullWidth>🍔 Блюда</Button>
                        </ListItem>
                        <ListItem>
                            <Button component={Link} to="/categories" fullWidth>📂 Категории</Button>
                        </ListItem>
                        <ListItem>
                            <Button component={Link} to="/ingredients" fullWidth>🥦 Ингредиенты</Button>
                        </ListItem>
                    </List>
                </Drawer>

                {/* Контент */}
                <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                    <Toolbar />
                    <Routes>
                    </Routes>
                </Box>
            </Box>
        </Router>
    );
}

export default App;
