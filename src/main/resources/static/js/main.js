// 全局变量
let currentUser = null;
let isLoading = false; // 添加加载状态标志

// API基础URL
const API_BASE_URL = 'http://localhost:8080/api';

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    // 检查本地存储中的用户信息
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
        currentUser = JSON.parse(storedUser);
        updateUIForLoggedInUser();
    }

    // 添加导航事件监听器
    document.querySelectorAll('[data-page]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const page = e.target.closest('.nav-link').dataset.page;
            navigateToPage(page);
        });
    });

    // 添加表单提交事件监听器
    document.getElementById('loginForm').addEventListener('submit', (e) => {
        e.preventDefault();
        login();
    });

    document.getElementById('registerForm').addEventListener('submit', (e) => {
        e.preventDefault();
        register();
    });

    // 默认加载树木页面
    navigateToPage('trees');
});

// 页面导航函数
function navigateToPage(page) {
    // 更新活动导航链接
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
        if (link.dataset.page === page) {
            link.classList.add('active');
        }
    });

    // 加载相应的页面内容
    switch (page) {
        case 'trees':
            loadTrees();
            break;
        case 'my-trees':
            if (!currentUser) {
                showLoginModal();
                return;
            }
            loadMyTrees();
            break;
        case 'shop':
            loadShop();
            break;
        case 'ranking':
            loadRanking();
            break;
    }
}

// 更新UI以显示已登录用户
function updateUIForLoggedInUser() {
    document.getElementById('loginButtons').style.display = 'none';
    document.getElementById('userButtons').style.display = 'block';
    document.getElementById('pointsDisplay').style.display = 'block';
    document.getElementById('welcomeUser').textContent = `欢迎, ${currentUser.username}`;
    document.getElementById('userPoints').textContent = currentUser.points || 0;
}

// 显示错误信息
function showError(message, modalId = null) {
    if (modalId) {
        const errorDiv = document.querySelector(`#${modalId} .alert-danger`);
        if (errorDiv) {
            errorDiv.textContent = message;
        } else {
            const div = document.createElement('div');
            div.className = 'alert alert-danger mt-3';
            div.textContent = message;
            document.querySelector(`#${modalId} .modal-body`).appendChild(div);
        }
    } else {
        const toast = document.createElement('div');
        toast.className = 'toast align-items-center text-white bg-danger border-0';
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;
        document.body.appendChild(toast);
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
        
        // 自动移除toast
        toast.addEventListener('hidden.bs.toast', () => {
            document.body.removeChild(toast);
        });
    }
}

// 清除错误信息
function clearError(modalId) {
    const errorDiv = document.querySelector(`#${modalId} .alert-danger`);
    if (errorDiv) {
        errorDiv.remove();
    }
}

// 登录功能
async function login() {
    clearError('loginModal');
    
    const form = document.getElementById('loginForm');
    const formData = new FormData(form);
    const data = {
        username: formData.get('username'),
        password: formData.get('password')
    };

    try {
        const response = await fetch(`${API_BASE_URL}/users/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const responseText = await response.text();
        let responseData;
        try {
            responseData = JSON.parse(responseText);
        } catch (e) {
            console.error('JSON解析错误:', e);
            console.error('原始响应:', responseText);
            throw new Error('服务器响应格式错误');
        }

        if (!response.ok) {
            throw new Error(responseData.message || '登录失败');
        }

        currentUser = responseData;
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        updateUIForLoggedInUser();
        
        // 关闭登录模态框
        const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
        modal.hide();
        
        // 重新加载当前页面
        navigateToPage(document.querySelector('.nav-link.active').dataset.page);
    } catch (error) {
        console.error('登录失败:', error);
        showError(error.message || '登录失败，请检查用户名和密码', 'loginModal');
    }
}

// 注册功能
async function register() {
    clearError('registerModal');
    
    const form = document.getElementById('registerForm');
    const formData = new FormData(form);
    const data = {
        username: formData.get('username'),
        password: formData.get('password'),
        email: formData.get('email'),
        phone: formData.get('phone')
    };

    try {
        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const responseData = await response.json();

        if (!response.ok) {
            throw new Error(responseData.message || '注册失败');
        }

        // 关闭注册模态框
        const registerModal = bootstrap.Modal.getInstance(document.getElementById('registerModal'));
        registerModal.hide();
        
        // 显示成功消息并打开登录模态框
        alert('注册成功，请登录');
        const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
        loginModal.show();
        
        // 清空注册表单
        form.reset();
    } catch (error) {
        console.error('注册失败:', error);
        showError(error.message || '注册失败，请检查输入信息', 'registerModal');
    }
}

// 退出登录
function logout() {
    currentUser = null;
    localStorage.removeItem('currentUser');
    document.getElementById('loginButtons').style.display = 'block';
    document.getElementById('userButtons').style.display = 'none';
    document.getElementById('pointsDisplay').style.display = 'none';
    navigateToPage('trees');
}

// 加载可认养树木
async function loadTrees() {
    const mainContent = document.getElementById('mainContent');
    mainContent.innerHTML = '<div class="text-center"><div class="spinner-border" role="status"><span class="visually-hidden">加载中...</span></div></div>';

    try {
        console.log('开始加载树木列表...');
        const response = await fetch(`${API_BASE_URL}/trees/available`);
        console.log('树木列表响应状态:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('服务器响应错误:', errorText);
            throw new Error(`获取树木列表失败: ${response.status} ${response.statusText}`);
        }

        const trees = await response.json();
        console.log('获取到树木数量:', trees.length);

        let html = `
            <div class="container mt-4">
                <h2 class="mb-4">树木认养列表</h2>
                <div class="row row-cols-1 row-cols-md-3 g-4">
        `;

        for (const tree of trees) {
            try {
                console.log('正在获取树木ID:', tree.id, '的认养信息');
                // 获取树木的认养信息
                const adoptionCountResponse = await fetch(`${API_BASE_URL}/trees/${tree.id}/adoption-count`);
                const adoptersResponse = await fetch(`${API_BASE_URL}/trees/${tree.id}/adopters`);
                
                if (!adoptionCountResponse.ok || !adoptersResponse.ok) {
                    console.error('获取认养信息失败:', tree.id);
                    console.error('认养数量响应:', adoptionCountResponse.status);
                    console.error('认养者响应:', adoptersResponse.status);
                    continue; // 跳过这棵树，继续处理其他树
                }

                const adoptionCount = await adoptionCountResponse.json();
                const adopters = await adoptersResponse.json();
                
                const adoptionStatus = adoptionCount > 0 
                    ? `<p class="text-muted">已有 ${adoptionCount} 人认养<br>认养者: ${adopters.join(', ')}</p>` 
                    : '<p class="text-success">等待认养</p>';
                
                html += `
                    <div class="col">
                        <div class="card h-100">
                            <div class="card-body">
                                <h5 class="card-title">${tree.name}</h5>
                                <p class="card-text">${tree.description}</p>
                                <p class="card-text"><small class="text-muted">位置: ${tree.location}</small></p>
                                ${adoptionStatus}
                                ${currentUser ? 
                                    `<button class="btn btn-success" onclick="adoptTree(${tree.id})">认养此树</button>` :
                                    `<button class="btn btn-primary" onclick="showLoginModal()">登录后认养</button>`
                                }
                            </div>
                        </div>
                    </div>
                `;
            } catch (error) {
                console.error('处理单棵树时出错:', error);
                continue; // 跳过这棵树，继续处理其他树
            }
        }

        html += `
                </div>
            </div>
        `;

        mainContent.innerHTML = html;
        console.log('树木列表加载完成');
    } catch (error) {
        console.error('加载树木失败:', error);
        mainContent.innerHTML = `
            <div class="container mt-4">
                <div class="alert alert-danger">
                    <h4 class="alert-heading">加载失败</h4>
                    <p>${error.message}</p>
                    <hr>
                    <p class="mb-0">
                        <button class="btn btn-primary" onclick="loadTrees()">重试</button>
                    </p>
                </div>
            </div>
        `;
    }
}

// 加载我的树木
async function loadMyTrees() {
    try {
        const response = await fetch(`${API_BASE_URL}/trees/user/${currentUser.id}`);
        const trees = await response.json();

        const mainContent = document.getElementById('mainContent');
        mainContent.innerHTML = `
            <div class="container">
                <div class="row mb-4">
                    <div class="col">
                        <h2 class="text-center mb-4">我的树木</h2>
                        <p class="text-center text-muted">您已认养 ${trees.length} 棵树</p>
                    </div>
                </div>
                ${trees.length === 0 ? `
                    <div class="text-center py-5">
                        <h4 class="text-muted">您还没有认养任何树木</h4>
                        <p class="mb-4">认养一棵树，为地球增添一份绿色</p>
                        <button class="btn btn-success btn-lg" onclick="navigateToPage('trees')">
                            去认养树木
                        </button>
                    </div>
                ` : `
                    <div class="row">
                        ${trees.map(tree => `
                            <div class="col-md-4 mb-4">
                                <div class="card tree-card">
                                    <div class="card-body">
                                        <h5 class="card-title">${tree.name}</h5>
                                        <p class="card-subtitle mb-2 text-muted">
                                            <i class="fas fa-map-marker-alt text-danger"></i> ${tree.location}
                                        </p>
                                        <p class="card-text">${tree.description}</p>
                                        <div class="text-center mb-3">
                                            ${getWateringStatus(tree.lastWateredTime)}
                                        </div>
                                        <button class="btn btn-info w-100" 
                                                onclick="waterTree(${tree.id})"
                                                ${canWaterToday(tree.lastWateredTime) ? '' : 'disabled'}>
                                            <i class="fas fa-tint"></i> 
                                            ${canWaterToday(tree.lastWateredTime) ? '浇水' : '今日已浇水'}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                `}
            </div>
        `;
    } catch (error) {
        console.error('加载我的树木失败:', error);
        showError('加载我的树木失败，请稍后重试');
    }
}

// 检查是否可以浇水
function canWaterToday(lastWateredTime) {
    if (!lastWateredTime) return true;
    const last = new Date(lastWateredTime);
    const now = new Date();
    return last.getDate() !== now.getDate() || 
           last.getMonth() !== now.getMonth() ||
           last.getFullYear() !== now.getFullYear();
}

// 获取浇水状态
function getWateringStatus(lastWateredTime) {
    if (!lastWateredTime) {
        return '<span class="text-warning"><i class="fas fa-exclamation-circle"></i> 待浇水</span>';
    }
    const canWater = canWaterToday(lastWateredTime);
    return canWater ? 
        '<span class="text-primary"><i class="fas fa-tint"></i> 可浇水</span>' : 
        '<span class="text-success"><i class="fas fa-check-circle"></i> 已浇水</span>';
}

// 显示登录模态框
function showLoginModal() {
    const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
    loginModal.show();
}

// 加载积分商城
async function loadShop() {
    try {
        const response = await fetch(`${API_BASE_URL}/products`);
        const products = await response.json();

        const mainContent = document.getElementById('mainContent');
        mainContent.innerHTML = `
            <h2 class="mb-4">积分商城</h2>
            <div class="row">
                ${products.map(product => `
                    <div class="col-md-4">
                        <div class="card product-card">
                            <img src="${product.imageUrl || 'https://via.placeholder.com/300x200'}" class="card-img-top product-image" alt="${product.name}">
                            <div class="card-body">
                                <h5 class="card-title">${product.name}</h5>
                                <p class="card-text">${product.description}</p>
                                <p class="card-text">
                                    <strong>所需积分: ${product.pointsRequired}</strong>
                                    <br>
                                    <small class="text-muted">库存: ${product.stock}</small>
                                </p>
                                ${currentUser ? `
                                    <button class="btn btn-primary" onclick="exchangeProduct(${product.id})"
                                            ${(!product.isAvailable || product.stock <= 0 || currentUser.points < product.pointsRequired) ? 'disabled' : ''}>
                                        ${!product.isAvailable || product.stock <= 0 ? '已售罄' : 
                                          currentUser.points < product.pointsRequired ? '积分不足' : '兑换'}
                                    </button>
                                ` : `
                                    <button class="btn btn-primary" disabled>请先登录</button>
                                `}
                            </div>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    } catch (error) {
        console.error('加载商城失败:', error);
        alert('加载商城失败');
    }
}

// 加载排行榜
async function loadRanking() {
    try {
        const response = await fetch(`${API_BASE_URL}/users/top`);
        const users = await response.json();

        const mainContent = document.getElementById('mainContent');
        mainContent.innerHTML = `
            <h2 class="mb-4">用户排行榜</h2>
            <div class="ranking-list">
                ${users.map((user, index) => `
                    <div class="ranking-item ${index < 3 ? 'top-3' : ''}">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <span class="h5 me-3">#${index + 1}</span>
                                <span>${user.username}</span>
                            </div>
                            <div>
                                <span class="badge bg-success">${user.points} 积分</span>
                            </div>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    } catch (error) {
        console.error('加载排行榜失败:', error);
        alert('加载排行榜失败');
    }
}

// 认养树木
async function adoptTree(treeId) {
    if (!currentUser) {
        showLoginModal();
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/trees/${treeId}/adopt`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: currentUser.id
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || '认养失败');
        }

        await response.json();
        alert('认养成功！');
        navigateToPage('my-trees');
    } catch (error) {
        console.error('认养树木失败:', error);
        alert(error.message || '认养树木失败，请稍后重试');
    }
}

// 浇水
async function waterTree(treeId) {
    if (!currentUser) {
        showLoginModal();
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/trees/${treeId}/water`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: currentUser.id
            })
        });

        if (!response.ok) {
            const error = await response.json();
            showError(error.message || '浇水失败');
            return;
        }

        const result = await response.json();
        currentUser.points += 10; // 更新本地积分
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        document.getElementById('userPoints').textContent = currentUser.points;
        
        showSuccess('浇水成功！获得10积分');
        await loadMyTrees(); // 重新加载我的树木页面
    } catch (error) {
        console.error('浇水失败:', error);
        showError(error.message || '浇水失败，请稍后重试');
    }
}

// 显示成功提示
function showSuccess(message) {
    const toast = document.createElement('div');
    toast.className = 'toast align-items-center text-white bg-success border-0';
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;
    document.body.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
    
    // 自动移除toast
    toast.addEventListener('hidden.bs.toast', () => {
        document.body.removeChild(toast);
    });
}

// 兑换商品
async function exchangeProduct(productId) {
    try {
        const response = await fetch(`${API_BASE_URL}/products/${productId}/exchange?userId=${currentUser.id}`, {
            method: 'POST'
        });

        if (!response.ok) {
            throw new Error('兑换失败');
        }

        // 重新获取用户信息以更新积分
        const userResponse = await fetch(`${API_BASE_URL}/users/${currentUser.id}`);
        currentUser = await userResponse.json();
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        document.getElementById('userPoints').textContent = currentUser.points;

        alert('兑换成功！');
        navigateToPage('shop');
    } catch (error) {
        console.error('兑换商品失败:', error);
        alert('兑换商品失败: ' + error.message);
    }
} 