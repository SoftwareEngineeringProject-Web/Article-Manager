function loadContent(url,htmlPage, currentPage) {
  const contentContainer = document.getElementById('content-container');
  contentContainer.innerHTML = ''; // 清空内容容器
  $('.sidebar a').removeClass('active'); // 移除所有链接的活动类
  $(`.sidebar a[href="#${htmlPage}"]`).addClass('active'); // 为当前链接添加活动类

  // 动态加载页面内容
  fetch(`${url}/${htmlPage}?page=${currentPage || 0}`)
      .then(response => response.text())
      .then(html => {
        contentContainer.innerHTML = html;
        $('#confirmDeleteModal').on('show.bs.modal', function (event) {
          var button = $(event.relatedTarget);        // 触发模态框的按钮
          var articleId = button.data('article-id');  // 从按钮中获取文章 id
          var articleTitle = button.data('article-title'); // 从按钮中获取文章标题
          var modal = $(this); // 找到模态框
          modal.find('.article-title').text("确定要删除《" + articleTitle + "》这篇文章吗？");  // 在模态框中显示文章标题
          var confirmDeleteButton = $(this).find('#confirmDeleteButton');   // 找到模态框中的确认删除按钮
          confirmDeleteButton.data('article-id', articleId);
        });
        $('#confirmDeleteButton').on('click', function () {
          var articleId = $(this).data('article-id');
          fetch(`delete-article/${articleId}`, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json'
            }
          }).then(response => {
            if (response.ok) {
              $('#confirmDeleteModal').modal('hide');
              loadContent(url,'manage-articles');
            } else {
              alert('删除失败');
            }
          });
        });

        const createdAtElems = document.querySelectorAll('[id^="created-at-"]');
        createdAtElems.forEach(elem => {
          let gmtDate = new Date(elem.textContent);
          elem.textContent = gmtDate.toLocaleString();
        });

        $('#confirmDeleteCommentModal').on('show.bs.modal', function (event) {
          var button = $(event.relatedTarget);        // 触发模态框的按钮
          var commentId = button.data('comment-id');
          var commentContent = button.data('comment-content');
          var modal = $(this);
          modal.find('.comment-content').text("确定要删除评论：\"" + commentContent + "\" 吗？");
          var confirmDeleteCommentButton = $(this).find('#confirmDeleteCommentButton');   // 找到模态框中的确认删除按钮
          confirmDeleteCommentButton.data('comment-id', commentId);
        });
        $('#confirmDeleteCommentButton').on('click', function () {
          var commentId = $(this).data('comment-id');
          fetch(`delete-comment/${commentId}`, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json'
            }
          }).then(response => {
            if (response.ok) {
              $('#confirmDeleteCommentModal').modal('hide');
              loadContent(url,'manage-comments');
            } else {
              alert('删除失败');
            }
          });
        });

        $('#confirmDeleteFavoriteModal').on('show.bs.modal', function (event) {
          var button = $(event.relatedTarget);
          var favoriteId = button.data('favorite-id');
          var favoriteName = button.data('favorite-name');
          var modal = $(this);
          modal.find('.favorite-content').text("确定要删除收藏夹：\"" + favoriteName + "\" 吗？");
          var confirmDeleteFavoriteButton = $(this).find('#confirmDeleteFavoriteButton');
          confirmDeleteFavoriteButton.data('favorite-id', favoriteId);
        });
        $('#confirmDeleteFavoriteButton').on('click', function () {
          var favoriteId = $(this).data('favorite-id');
          fetch(`delete-favorite/${favoriteId}`, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json'
            }
          }).then(response => {
            if (response.ok) {
              $('#confirmDeleteFavoriteModal').modal('hide');
              loadContent(url,'manage-favorites');
            } else {
              alert('删除失败');
            }
          });
        });

        $('#editFavoriteModal').on('show.bs.modal', function (event) {
          var button = $(event.relatedTarget);
          var favoriteId = button.data('favorite-id');
          var favoriteName = button.data('favorite-name');
          document.getElementById('editFavoriteName').value = favoriteName; // 显示当前收藏夹名称
          var editFavoriteButton = $(this).find('#editFavoriteButton');
          editFavoriteButton.data('favorite-id', favoriteId);
        });
        $('#editFavoriteButton').on('click', function () {
          var favoriteId = $(this).data('favorite-id');
          const favoriteName = document.getElementById('editFavoriteName').value;
          fetch(`edit-favorite/${favoriteId}`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({name: favoriteName})
          }).then(response => {
            if (response.ok) {
              $('#editFavoriteModal').modal('hide');
              loadContent(url,'manage-favorites');
            } else {
              alert('修改失败');
            }
          });
        });

        $('#confirmRemoveFromFavoriteModal').on('show.bs.modal', function (event) {
          var button = $(event.relatedTarget);
          var favoriteId = button.data('favorite-id');
          var articleId = button.data('article-id');
          var modal = $(this);
          modal.find('.favorite-article-content').text("确定要将文章《" + button.data('article-title') + "》从收藏夹\"" + button.data('favorite-name') + "\" 移除吗？");
          var confirmRemoveFromFavoriteButton = $(this).find('#confirmRemoveFromFavoriteButton');
          confirmRemoveFromFavoriteButton.data('favorite-id', favoriteId);
          confirmRemoveFromFavoriteButton.data('article-id', articleId);
        });
        $('#confirmRemoveFromFavoriteButton').on('click', function () {
          var favoriteId = $(this).data('favorite-id');
          var articleId = $(this).data('article-id');
          fetch(`delete-article-from-favorite/${favoriteId}/${articleId}`, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json'
            },
          }).then(response => {
            if (response.ok) {
              $('#confirmRemoveFromFavoriteModal').modal('hide');
              loadContent(url,'manage-favorites');
            } else {
              alert('删除失败');
            }
          });

          $('#confirmDeleteUserModal').on('show.bs.modal', function (event) {
            var button = $(event.relatedTarget);
            var username = button.data('user-username');
            var userId = button.data('user-id');
            var modal = $(this);
            modal.find('.favorite-content').text("确定要删除用户：\"" + username + "\" 吗？");
            var confirmDeleteFavoriteButton = $(this).find('#confirmDeleteUserButton');
            confirmDeleteFavoriteButton.data('user-id', userId);
          });
          $('#confirmDeleteUserButton').on('click', function () {
            var userId = $(this).data('user-id');
            fetch(`/{username}/delete-favorite/${userId}`, {
              method: 'DELETE',
              headers: {
                'Content-Type': 'application/json'
              }
            }).then(response => {
              if (response.ok) {
                $('#confirmDeleteUserModal').modal('hide');
                loadContent(url,'manage-users');
              } else {
                alert('删除失败');
              }
            });
          });
        });
      })
      .catch(error => console.error('Error loading content:', error));
}
