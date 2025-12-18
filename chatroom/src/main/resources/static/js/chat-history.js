// chat-history.js
const roomId = new URLSearchParams(window.location.search).get('room') || 'room_general';
const pageSize = 50;
const messagesEl = document.getElementById('messages');
const wrapperEl = document.getElementById('chat-wrapper');
const skeletonEl = document.getElementById('loading-skeleton');
const topSentinel = document.getElementById('top-sentinel');
let currentPage = 0;
let isLoading = false;
let noMore = false;

async function init() {
  showSkeleton(true);
  await loadPage(0, { initial: true });
  showSkeleton(false);
  // scroll to bottom
  wrapperEl.scrollTop = wrapperEl.scrollHeight;
  observeTop();
}

async function loadPage(page, options = {}) {
  if (isLoading || noMore) return;
  isLoading = true;
  try {
    const res = await fetch(`/api/messages/${encodeURIComponent(roomId)}?page=${page}&size=${pageSize}`, { credentials: 'same-origin' });
    if (!res.ok) throw new Error('Failed to load messages: ' + res.status);
    const pageObj = await res.json();
    const items = pageObj.content || [];
    if (items.length === 0) {
      if (page > 0) noMore = true;
      isLoading = false;
      return;
    }
    const messagesAsc = items.slice().reverse();
    if (options.initial) {
      renderMessagesAppend(messagesAsc, { scrollToBottom: true });
    } else {
      const prevScroll = wrapperEl.scrollHeight;
      renderMessagesPrepend(messagesAsc);
      const newScroll = wrapperEl.scrollHeight;
      wrapperEl.scrollTop = newScroll - prevScroll;
    }
    currentPage = Math.max(currentPage, page);
    if (items.length < pageSize) noMore = true;
  } catch (err) {
    console.error('loadPage error', err);
  } finally {
    isLoading = false;
  }
}

function renderMessagesAppend(messages, { scrollToBottom = false } = {}) {
  const fragment = document.createDocumentFragment();
  let lastGroup = null;
  messages.forEach(msg => {
    const key = dateGroupKey(msg.createdAt);
    if (key !== lastGroup) {
      fragment.appendChild(createDateSeparator(key, msg.createdAt));
      lastGroup = key;
    }
    fragment.appendChild(createMessageElement(msg));
  });
  messagesEl.appendChild(fragment);
  if (scrollToBottom) setTimeout(() => { wrapperEl.scrollTop = wrapperEl.scrollHeight; }, 10);
}

function renderMessagesPrepend(messages) {
  if (messages.length === 0) return;
  const first = messagesEl.firstChild;
  const frag = document.createDocumentFragment();
  let lastGroup = null;
  messages.forEach(msg => {
    const key = dateGroupKey(msg.createdAt);
    if (key !== lastGroup) {
      frag.appendChild(createDateSeparator(key, msg.createdAt));
      lastGroup = key;
    }
    frag.appendChild(createMessageElement(msg));
  });
  messagesEl.insertBefore(frag, first);
}

function createMessageElement(msg) {
  const div = document.createElement('div');
  div.className = 'message' + (isOwnMessage(msg) ? ' own' : '');
  div.dataset.id = msg.id;
  const name = document.createElement('div'); name.style.fontSize='12px'; name.style.fontWeight='600'; name.style.marginBottom='4px'; name.textContent = msg.senderName || '匿名';

  const body = document.createElement('div');
  if (msg.contentType === 'IMAGE') {
      const img = document.createElement('img');
      img.src = msg.content;
      img.style.maxWidth = '200px';
      img.style.cursor = 'pointer';
      img.style.display = 'block';
      img.onclick = () => {
          // Assuming lightbox exists in parent or we create a simple one
          window.open(msg.content, '_blank');
      };
      body.appendChild(img);
  } else if (msg.contentType === 'FILE') {
      const link = document.createElement('a');
      link.href = msg.content;
      link.target = '_blank';
      link.textContent = '📄 ' + (msg.content.split('/').pop() || 'File');
      body.appendChild(link);
  } else {
      body.textContent = msg.content;
  }

  const meta = document.createElement('div'); meta.style.fontSize='11px'; meta.style.color='#888'; meta.style.marginTop='6px'; meta.textContent = formatTime(msg.createdAt);
  div.appendChild(name); div.appendChild(body); div.appendChild(meta);
  return div;
}

function createDateSeparator(key, createdAtStr) { const d = document.createElement('div'); d.className='date-sep'; d.textContent = humanFriendlyDateLabel(createdAtStr); return d; }
function isOwnMessage(msg) { const myId = localStorage.getItem('userId'); return myId && String(myId) === String(msg.senderId); }
function formatTime(iso) { const d=new Date(iso); return d.toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'}); }
function dateGroupKey(iso){ const d=new Date(iso); const y=d.getFullYear(); const m=(d.getMonth()+1).toString().padStart(2,'0'); const day=d.getDate().toString().padStart(2,'0'); return `${y}-${m}-${day}`; }
function humanFriendlyDateLabel(iso){ const d=new Date(iso); const now=new Date(); const today=new Date(now.getFullYear(),now.getMonth(),now.getDate()).getTime(); const t=new Date(d.getFullYear(),d.getMonth(),d.getDate()).getTime(); const dayMs=24*3600*1000; if (t===today) return '今天'; if (t===today-dayMs) return '昨天'; return d.toLocaleDateString(); }

function showSkeleton(show){ if(!skeletonEl) return; skeletonEl.innerHTML=''; if(!show){ skeletonEl.classList.add('hidden'); return;} skeletonEl.classList.remove('hidden'); for(let i=0;i<6;i++){ const line=document.createElement('div'); line.className='line '+(i%2?'long':'short'); skeletonEl.appendChild(line); }}

function observeTop(){ const options={ root: wrapperEl, threshold: 0.05 }; const io=new IntersectionObserver(entries=>{ entries.forEach(entry=>{ if(entry.isIntersecting){ if(!isLoading && !noMore){ loadPage(currentPage+1, { initial:false }); } } }); }, options); io.observe(topSentinel); }

init();

