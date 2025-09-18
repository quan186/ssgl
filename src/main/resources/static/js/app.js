// 自定义JavaScript

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化工具提示
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // 初始化弹出框
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // 自动隐藏警告消息
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
    
    // 确认删除对话框
    var deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            if (!confirm('确定要删除吗？此操作不可撤销。')) {
                e.preventDefault();
            }
        });
    });
    
    // 表单验证
    var forms = document.querySelectorAll('.needs-validation');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
    
    // 搜索功能
    var searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(function(input) {
        input.addEventListener('input', function() {
            var searchTerm = this.value.toLowerCase();
            var targetTable = document.querySelector(this.dataset.target);
            
            if (targetTable) {
                var rows = targetTable.querySelectorAll('tbody tr');
                rows.forEach(function(row) {
                    var text = row.textContent.toLowerCase();
                    if (text.includes(searchTerm)) {
                        row.style.display = '';
                    } else {
                        row.style.display = 'none';
                    }
                });
            }
        });
    });
    
    // 表格排序功能
    var sortableHeaders = document.querySelectorAll('.sortable');
    sortableHeaders.forEach(function(header) {
        header.addEventListener('click', function() {
            var table = this.closest('table');
            var tbody = table.querySelector('tbody');
            var rows = Array.from(tbody.querySelectorAll('tr'));
            var columnIndex = Array.from(this.parentNode.children).indexOf(this);
            var isAscending = this.classList.contains('sort-asc');
            
            // 移除所有排序类
            sortableHeaders.forEach(function(h) {
                h.classList.remove('sort-asc', 'sort-desc');
            });
            
            // 添加当前排序类
            this.classList.add(isAscending ? 'sort-desc' : 'sort-asc');
            
            // 排序行
            rows.sort(function(a, b) {
                var aText = a.children[columnIndex].textContent.trim();
                var bText = b.children[columnIndex].textContent.trim();
                
                // 尝试数字比较
                var aNum = parseFloat(aText);
                var bNum = parseFloat(bText);
                
                if (!isNaN(aNum) && !isNaN(bNum)) {
                    return isAscending ? bNum - aNum : aNum - bNum;
                }
                
                // 文本比较
                return isAscending ? bText.localeCompare(aText) : aText.localeCompare(bText);
            });
            
            // 重新插入排序后的行
            rows.forEach(function(row) {
                tbody.appendChild(row);
            });
        });
    });
    
    // 模态框表单提交
    var modalForms = document.querySelectorAll('.modal form');
    modalForms.forEach(function(form) {
        form.addEventListener('submit', function(e) {
            var submitButton = form.querySelector('button[type="submit"]');
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.innerHTML = '<span class="loading"></span> 处理中...';
            }
        });
    });
    
    // 图片预览功能
    var imageInputs = document.querySelectorAll('input[type="file"][accept*="image"]');
    imageInputs.forEach(function(input) {
        input.addEventListener('change', function() {
            var file = this.files[0];
            if (file) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    var preview = document.querySelector('#' + input.id + '-preview');
                    if (preview) {
                        preview.src = e.target.result;
                        preview.style.display = 'block';
                    }
                };
                reader.readAsDataURL(file);
            }
        });
    });
    
    // 日期选择器初始化
    var dateInputs = document.querySelectorAll('input[type="date"]');
    dateInputs.forEach(function(input) {
        if (!input.value) {
            input.value = new Date().toISOString().split('T')[0];
        }
    });
    
    // 数字输入框格式化
    var numberInputs = document.querySelectorAll('input[type="number"]');
    numberInputs.forEach(function(input) {
        input.addEventListener('blur', function() {
            if (this.value && !isNaN(this.value)) {
                this.value = parseFloat(this.value).toFixed(2);
            }
        });
    });
    
    // 复制到剪贴板功能
    var copyButtons = document.querySelectorAll('.btn-copy');
    copyButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var text = this.dataset.copyText || this.textContent;
            navigator.clipboard.writeText(text).then(function() {
                // 显示成功提示
                var toast = document.createElement('div');
                toast.className = 'toast align-items-center text-white bg-success border-0';
                toast.setAttribute('role', 'alert');
                toast.innerHTML = `
                    <div class="d-flex">
                        <div class="toast-body">
                            <i class="fas fa-check me-2"></i>已复制到剪贴板
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                    </div>
                `;
                
                var toastContainer = document.querySelector('.toast-container') || document.body;
                toastContainer.appendChild(toast);
                
                var bsToast = new bootstrap.Toast(toast);
                bsToast.show();
                
                // 自动移除toast元素
                toast.addEventListener('hidden.bs.toast', function() {
                    toast.remove();
                });
            });
        });
    });
});

// 工具函数
window.AppUtils = {
    // 格式化日期
    formatDate: function(date, format = 'YYYY-MM-DD') {
        if (!date) return '';
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        
        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day);
    },
    
    // 格式化货币
    formatCurrency: function(amount, currency = '¥') {
        if (!amount) return currency + '0.00';
        return currency + parseFloat(amount).toFixed(2);
    },
    
    // 显示加载状态
    showLoading: function(element) {
        if (element) {
            element.disabled = true;
            element.innerHTML = '<span class="loading"></span> 加载中...';
        }
    },
    
    // 隐藏加载状态
    hideLoading: function(element, originalText) {
        if (element) {
            element.disabled = false;
            element.innerHTML = originalText || '提交';
        }
    },
    
    // 显示成功消息
    showSuccess: function(message) {
        this.showAlert(message, 'success');
    },
    
    // 显示错误消息
    showError: function(message) {
        this.showAlert(message, 'danger');
    },
    
    // 显示警告消息
    showWarning: function(message) {
        this.showAlert(message, 'warning');
    },
    
    // 显示信息消息
    showInfo: function(message) {
        this.showAlert(message, 'info');
    },
    
    // 显示警告框
    showAlert: function(message, type = 'info') {
        const alertContainer = document.querySelector('.alert-container') || document.querySelector('main .container');
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show`;
        alert.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-circle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        alertContainer.insertBefore(alert, alertContainer.firstChild);
        
        // 自动隐藏
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    },
    
    // 确认对话框
    confirm: function(message, callback) {
        if (confirm(message)) {
            callback();
        }
    },
    
    // 获取URL参数
    getUrlParameter: function(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    },
    
    // 设置URL参数
    setUrlParameter: function(name, value) {
        const url = new URL(window.location);
        url.searchParams.set(name, value);
        window.history.pushState({}, '', url);
    }
};
